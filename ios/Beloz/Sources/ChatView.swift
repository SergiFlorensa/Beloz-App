import SwiftUI

struct ChatView: View {
    @EnvironmentObject private var store: BelozStore
    @State private var draft = ""
    @State private var didApplyInitialPrompt = false
    var initialPrompt: String? = nil

    private let prompts = ["Tengo 12 € y voy con prisa", "Quiero algo ligero", "Sorpréndeme para cenar"]

    var body: some View {
        ScreenBackdrop {
            VStack(spacing: 0) {
                HStack(spacing: 11) {
                    Image(systemName: "sparkles")
                        .font(.system(size: 17, weight: .bold)).foregroundStyle(BelozStyle.forest)
                        .frame(width: 42, height: 42).background(BelozStyle.mint, in: RoundedRectangle(cornerRadius: 14))
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Beloz AI").font(.system(size: 17, weight: .bold, design: .rounded))
                        Text("Ideas basadas en restaurantes y carta").font(.caption).foregroundStyle(BelozStyle.muted)
                    }
                    Spacer()
                    Button { store.resetChat() } label: {
                        Image(systemName: "arrow.counterclockwise").foregroundStyle(BelozStyle.muted).frame(width: 40, height: 40)
                    }
                    .accessibilityLabel("Nueva conversación")
                }
                .padding(.horizontal, 16).padding(.vertical, 10)
                .background(.white)

                ScrollViewReader { proxy in
                    ScrollView(showsIndicators: false) {
                        LazyVStack(spacing: 14) {
                            if store.chatMessages.count == 1 {
                                VStack(alignment: .leading, spacing: 12) {
                                    Text("¿Qué te apetece?").font(.system(size: 26, weight: .bold, design: .rounded))
                                    Text("Dime presupuesto, antojo o restaurante. Te propongo opciones del catálogo disponible.")
                                        .font(.subheadline).foregroundStyle(BelozStyle.muted).fixedSize(horizontal: false, vertical: true)
                                    ScrollView(.horizontal, showsIndicators: false) {
                                        HStack(spacing: 8) {
                                            ForEach(prompts, id: \.self) { prompt in
                                                Button(prompt) { send(prompt) }
                                                    .font(.caption.weight(.semibold)).foregroundStyle(BelozStyle.forest)
                                                    .padding(.horizontal, 13).padding(.vertical, 10)
                                                    .background(.white, in: Capsule())
                                                    .overlay(Capsule().stroke(BelozStyle.line, lineWidth: 1))
                                            }
                                        }
                                    }
                                }
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(.horizontal, 16).padding(.top, 20).padding(.bottom, 4)
                            }

                            ForEach(store.chatMessages) { message in
                                MessageBubble(message: message)
                                    .id(message.id)
                            }
                            if store.isSendingChat {
                                HStack(spacing: 9) {
                                    ProgressView().tint(BelozStyle.green)
                                    Text("Consultando opciones…").font(.caption).foregroundStyle(BelozStyle.muted)
                                    Spacer()
                                }
                                .padding(13).frame(maxWidth: 290, alignment: .leading)
                                .background(.white, in: RoundedRectangle(cornerRadius: 17))
                                .frame(maxWidth: .infinity, alignment: .leading).padding(.horizontal, 16)
                                .id("loading")
                            }
                            if let error = store.chatError {
                                VStack(alignment: .leading, spacing: 8) {
                                    InlineNotice(text: error, isError: true)
                                    Button("Reintentar") { Task { await store.retryChat() } }
                                        .font(.caption.weight(.semibold)).foregroundStyle(BelozStyle.green)
                                }
                                .padding(.horizontal, 16)
                            }
                        }
                        .padding(.bottom, 16)
                    }
                    .onChange(of: store.chatMessages.count) { _, _ in
                        if let last = store.chatMessages.last { withAnimation(.easeOut(duration: 0.2)) { proxy.scrollTo(last.id, anchor: .bottom) } }
                    }
                    .onChange(of: store.isSendingChat) { _, sending in
                        if sending { withAnimation { proxy.scrollTo("loading", anchor: .bottom) } }
                    }
                }

                HStack(alignment: .bottom, spacing: 10) {
                    TextField("Pregunta qué pedir…", text: $draft, axis: .vertical)
                        .lineLimit(1...5).font(.body).padding(.vertical, 13).padding(.horizontal, 15)
                        .background(BelozStyle.canvas, in: RoundedRectangle(cornerRadius: 20))
                        .overlay(RoundedRectangle(cornerRadius: 20).stroke(BelozStyle.line, lineWidth: 1))
                        .onSubmit { send(draft) }
                    Button { send(draft) } label: {
                        Image(systemName: "arrow.up")
                            .font(.system(size: 16, weight: .bold)).foregroundStyle(.white)
                            .frame(width: 46, height: 46).background(BelozStyle.forest, in: Circle())
                    }
                    .disabled(store.isSendingChat || draft.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                    .opacity(store.isSendingChat || draft.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? 0.5 : 1)
                    .accessibilityLabel("Enviar mensaje")
                }
                .padding(.horizontal, 13).padding(.vertical, 10)
                .background(.white)
            }
        }
        .navigationTitle("Beloz AI")
        .navigationBarTitleDisplayMode(.inline)
        .task {
            if let initialPrompt, !initialPrompt.isEmpty, !didApplyInitialPrompt {
                didApplyInitialPrompt = true
                if store.isSendingChat { draft = initialPrompt }
                else { send(initialPrompt) }
            }
        }
    }

    private func send(_ text: String) {
        let cleaned = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !cleaned.isEmpty else { return }
        draft = ""
        Task { await store.sendChat(cleaned) }
    }
}

private struct MessageBubble: View {
    @EnvironmentObject private var store: BelozStore
    let message: ConversationMessage

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text(message.text)
                .font(.system(size: 15, weight: .regular))
                .lineSpacing(3)
                .fixedSize(horizontal: false, vertical: true)
                .foregroundStyle(message.role == .user ? .white : BelozStyle.ink)
            if message.role == .assistant {
                ForEach(message.suggestions.filter { $0.restaurantId > 0 }) { suggestion in
                    suggestionView(suggestion)
                }
                if !message.sources.isEmpty {
                    Text("Información del catálogo · \(message.sources.compactMap(\.label).joined(separator: ", "))")
                        .font(.caption2).foregroundStyle(BelozStyle.muted)
                        .lineLimit(2)
                }
                if let provider = message.provider, !provider.isEmpty {
                    Text("Beloz AI · \(providerLabel(provider))").font(.system(size: 10, weight: .medium)).foregroundStyle(BelozStyle.green)
                }
            }
        }
        .padding(14)
        .frame(maxWidth: 325, alignment: .leading)
        .background(message.role == .user ? BelozStyle.forest : .white,
                    in: RoundedRectangle(cornerRadius: 19, style: .continuous))
        .overlay(alignment: message.role == .user ? .bottomTrailing : .bottomLeading) {
            UnevenRoundedRectangle(
                topLeadingRadius: 3,
                bottomLeadingRadius: message.role == .assistant ? 5 : 19,
                bottomTrailingRadius: message.role == .user ? 5 : 19,
                topTrailingRadius: 19
            )
            .fill(message.role == .user ? BelozStyle.forest : .white)
            .frame(width: 12, height: 12).offset(y: 4)
        }
        .frame(maxWidth: .infinity, alignment: message.role == .user ? .trailing : .leading)
        .padding(.horizontal, 16)
    }

    @ViewBuilder
    private func suggestionView(_ suggestion: ChatSuggestion) -> some View {
        let restaurant = store.restaurants.first { $0.id == suggestion.restaurantId }
        let card = HStack(spacing: 10) {
            RemoteFoodImage(path: suggestion.imagePath ?? restaurant?.imagePath, height: 54)
                .frame(width: 58).clipShape(RoundedRectangle(cornerRadius: 10))
            VStack(alignment: .leading, spacing: 3) {
                Text(suggestion.dishName ?? suggestion.restaurantName)
                    .font(.system(size: 13, weight: .semibold)).foregroundStyle(BelozStyle.ink).lineLimit(1)
                Text(suggestion.restaurantName).font(.caption).foregroundStyle(BelozStyle.muted).lineLimit(1)
                if let reason = suggestion.reason, !reason.isEmpty {
                    Text(reason).font(.caption2).foregroundStyle(BelozStyle.muted).lineLimit(2)
                }
            }
            Spacer(minLength: 0)
            if let price = suggestion.price { Text(price.euro).font(.caption.weight(.bold)).foregroundStyle(BelozStyle.forest) }
        }
        .padding(8)
        .background(BelozStyle.canvas, in: RoundedRectangle(cornerRadius: 13))
        if let restaurant {
            NavigationLink(destination: RestaurantDetailView(restaurant: restaurant)) { card }
                .buttonStyle(.plain)
        } else { card }
    }

    private func providerLabel(_ provider: String) -> String {
        if provider.localizedCaseInsensitiveContains("ollama") { return "modelo local" }
        if provider.localizedCaseInsensitiveContains("local") || provider.localizedCaseInsensitiveContains("beloz") { return "motor Beloz" }
        return provider
    }
}
