import SwiftUI

struct ChatView: View {
    @EnvironmentObject private var store: BelozStore
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var draft = ""
    @State private var didApplyInitialPrompt = false
    var initialPrompt: String? = nil

    private let prompts = [
        ("Platos hasta 12 EUR", "Recomiéndame platos de hasta 12 EUR"),
        ("Quiero cenar rápido", "Quiero cenar rápido"),
        ("Ver una carta", "Quiero ver una carta")
    ]

    var body: some View {
        ScreenBackdrop {
            VStack(spacing: 0) {
                ScrollViewReader { proxy in
                    ScrollView {
                        LazyVStack(alignment: .leading, spacing: 24) {
                            ForEach(store.chatMessages) { message in
                                ChatMessageBlock(message: message).id(message.id)
                            }
                            if store.isSendingChat {
                                HStack(spacing: 12) {
                                    ProgressView().tint(BelozStyle.green)
                                    Text("Esperando respuesta…").foregroundStyle(BelozStyle.muted)
                                }
                                .font(.system(size: 16)).accessibilityLabel("Esperando respuesta")
                                .id("loading")
                            }
                            if let error = store.chatError {
                                VStack(alignment: .leading, spacing: 6) {
                                    Text(error).foregroundStyle(BelozStyle.danger)
                                    Button {
                                        Task { await store.retryChat() }
                                    } label: {
                                        Label("Reintentar", systemImage: "arrow.clockwise")
                                    }
                                    .frame(minHeight: 48).foregroundStyle(BelozStyle.green)
                                }.font(.system(size: 16)).id("chat-error")
                            }
                            if !store.chatMessages.contains(where: { $0.role == .user }) {
                                VStack(alignment: .leading, spacing: 4) {
                                    ForEach(prompts.indices, id: \.self) { index in
                                        Button { send(prompts[index].1) } label: {
                                            HStack(spacing: 12) {
                                                Text(prompts[index].0)
                                                Image(systemName: "arrow.right")
                                            }
                                            .frame(minHeight: 48).contentShape(Rectangle())
                                        }
                                        .disabled(store.isSendingChat)
                                    }
                                }
                                .font(.system(size: 16)).foregroundStyle(BelozStyle.green)
                            }
                        }
                        .frame(maxWidth: 720, alignment: .leading)
                        .padding(.horizontal, 20).padding(.vertical, 20)
                        .frame(maxWidth: .infinity)
                    }
                    .onChange(of: store.chatMessages.count) { _, _ in
                        if let last = store.chatMessages.last {
                            withAnimation(reduceMotion ? nil : .easeOut(duration: 0.2)) {
                                proxy.scrollTo(last.id, anchor: .bottom)
                            }
                        }
                    }
                    .onChange(of: store.isSendingChat) { _, sending in
                        if sending {
                            withAnimation(reduceMotion ? nil : .easeOut(duration: 0.2)) {
                                proxy.scrollTo("loading", anchor: .bottom)
                            }
                        }
                    }
                }

                HStack(alignment: .bottom, spacing: 8) {
                    TextField("¿Qué te apetece?", text: $draft, axis: .vertical)
                        .lineLimit(1...4).font(.system(size: 16))
                        .padding(.horizontal, 12).padding(.vertical, 14)
                        .background(.white, in: RoundedRectangle(cornerRadius: 8))
                        .overlay(RoundedRectangle(cornerRadius: 8).stroke(BelozStyle.line, lineWidth: 1))
                        .submitLabel(.send).onSubmit { send(draft) }
                    Button { send(draft) } label: {
                        Image(systemName: "paperplane.fill")
                            .font(.system(size: 18, weight: .semibold)).foregroundStyle(.white)
                            .frame(width: 48, height: 48)
                            .background(BelozStyle.green, in: RoundedRectangle(cornerRadius: 8))
                    }
                    .disabled(store.isSendingChat || draft.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty)
                    .opacity(store.isSendingChat || draft.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty ? 0.5 : 1)
                    .accessibilityLabel("Enviar mensaje")
                }
                .frame(maxWidth: 720).padding(.horizontal, 16).padding(.vertical, 10)
                .frame(maxWidth: .infinity).background(.white)
            }
        }
        .navigationTitle("Beloz")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button { store.resetChat() } label: { Image(systemName: "arrow.counterclockwise") }
                    .accessibilityLabel("Nueva conversación")
                    .disabled(store.isSendingChat)
            }
        }
        .task {
            if let initialPrompt, !initialPrompt.isEmpty, !didApplyInitialPrompt {
                didApplyInitialPrompt = true
                if store.isSendingChat { draft = initialPrompt }
                else { send(initialPrompt) }
            }
            if store.restaurants.isEmpty { await store.loadRestaurants() }
        }
    }

    private func send(_ text: String) {
        let cleaned = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !cleaned.isEmpty, !store.isSendingChat else { return }
        draft = ""
        Task { await store.sendChat(cleaned) }
    }
}

private struct ChatMessageBlock: View {
    let message: ConversationMessage

    var body: some View {
        let user = message.role == .user
        VStack(alignment: user ? .trailing : .leading, spacing: 8) {
            Text(user ? "Tú" : "Beloz")
                .font(.system(size: 12, weight: .semibold)).foregroundStyle(BelozStyle.muted)
            if user {
                Text(message.text)
                    .font(.system(size: 16)).lineSpacing(4).textSelection(.enabled)
                    .padding(12).frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(hex: 0xBFEFD9), in: RoundedRectangle(cornerRadius: 8))
                    .padding(.leading, 28)
            } else {
                Text(message.text)
                    .font(.system(size: 16)).lineSpacing(4).textSelection(.enabled)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
            if !user {
                ForEach(Array(message.suggestions.filter { $0.restaurantId > 0 }.prefix(3))) { suggestion in
                    ChatSuggestionCard(suggestion: suggestion)
                }
                if message.suggestions.isEmpty {
                    ForEach(message.sources.indices, id: \.self) { index in
                        ChatSourceNote(source: message.sources[index])
                    }
                }
                if let provider = message.provider, !provider.isEmpty {
                    Text(provider.localizedCaseInsensitiveContains("ollama") ? "Modelo local" : "Motor Beloz")
                        .font(.system(size: 11)).foregroundStyle(BelozStyle.muted)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: user ? .trailing : .leading)
    }
}

private struct ChatSuggestionCard: View {
    @EnvironmentObject private var store: BelozStore
    let suggestion: ChatSuggestion

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack(alignment: .top, spacing: 12) {
                if let path = suggestion.imagePath, !path.isEmpty {
                    RemoteFoodImage(path: path, height: 56).frame(width: 56)
                        .clipShape(RoundedRectangle(cornerRadius: 6))
                }
                VStack(alignment: .leading, spacing: 3) {
                    Text(suggestion.dishName ?? suggestion.restaurantName).font(.system(size: 16, weight: .semibold))
                    if suggestion.dishName != nil {
                        Text(suggestion.restaurantName).font(.caption).foregroundStyle(BelozStyle.muted)
                    }
                    Text(suggestion.price.map { $0.euro } ?? "Precio no disponible")
                        .font(.subheadline.weight(.semibold)).foregroundStyle(BelozStyle.green)
                    if let wait = suggestion.waitTime, wait > 0 {
                        Text("Espera registrada: \(wait) min").font(.caption).foregroundStyle(BelozStyle.muted)
                    }
                }
            }
            ChatSourceNote(source: suggestion.source)
            if let restaurant = store.restaurants.first(where: { $0.id == suggestion.restaurantId }) {
                NavigationLink(destination: RestaurantDetailView(restaurant: restaurant)) {
                    Label("Ver carta", systemImage: "arrow.right")
                        .frame(minHeight: 44)
                }.foregroundStyle(BelozStyle.green)
            }
        }
        .padding(14).frame(maxWidth: .infinity, alignment: .leading)
        .background(.white, in: RoundedRectangle(cornerRadius: 8))
        .overlay(RoundedRectangle(cornerRadius: 8).stroke(BelozStyle.line, lineWidth: 1))
    }
}

private struct ChatSourceNote: View {
    let source: ChatSource?

    private var safeURL: URL? {
        guard let raw = source?.url, let parts = URLComponents(string: raw),
              parts.scheme?.lowercased() == "https", parts.host != nil,
              parts.user == nil, parts.password == nil else { return nil }
        return parts.url
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 3) {
            Text(source?.label ?? "Catálogo Beloz")
            Text(source?.updatedAt.map { "Registro actualizado: \($0)" } ?? "Fecha de actualización no disponible")
            if let safeURL {
                Link("Abrir fuente", destination: safeURL)
                    .foregroundStyle(BelozStyle.green).frame(minHeight: 44)
            }
        }.font(.caption).foregroundStyle(BelozStyle.muted)
    }
}
