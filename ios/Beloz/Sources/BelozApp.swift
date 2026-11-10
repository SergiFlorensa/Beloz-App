import SwiftUI

@main
struct BelozApp: App {
    @StateObject private var store = BelozStore()

    var body: some Scene {
        WindowGroup {
            BelozLaunchRoot()
                .environmentObject(store)
                .tint(BelozStyle.green)
                .preferredColorScheme(.light)
        }
    }
}

private struct BelozLaunchRoot: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    @State private var showingIntro = true

    var body: some View {
        ZStack {
            BelozTabs().accessibilityHidden(showingIntro)
            if showingIntro {
                GeometryReader { geometry in
                    ZStack(alignment: .top) {
                        Image("BelozSplash").resizable().scaledToFill()
                            .frame(width: geometry.size.width, height: geometry.size.height).clipped()
                        HStack(spacing: 0) {
                            Text("Be").foregroundStyle(Color(hex: 0x1AB89E))
                            Text("Loz").foregroundStyle(Color(hex: 0xEBCA5F))
                        }
                        .font(.custom("VintageClamshell", size: 68, relativeTo: .largeTitle))
                        .minimumScaleFactor(0.6).lineLimit(1)
                        .shadow(color: .black.opacity(0.35), radius: 10, x: 2, y: 2)
                        .padding(.horizontal, 24).padding(.top, 55)
                    }
                    .frame(width: geometry.size.width, height: geometry.size.height)
                }
                .ignoresSafeArea()
                .accessibilityElement(children: .ignore).accessibilityLabel("Beloz")
                .transition(.opacity)
            }
        }
        .task {
            try? await Task.sleep(nanoseconds: 1_000_000_000)
            guard !Task.isCancelled else { return }
            withAnimation(reduceMotion ? nil : .easeOut(duration: 0.2)) { showingIntro = false }
        }
    }
}

private enum AppTab: Hashable {
    case home, explore, assistant, cart, account
}

struct BelozTabs: View {
    @EnvironmentObject private var store: BelozStore
    @State private var selection: AppTab = .home

    var body: some View {
        TabView(selection: $selection) {
            NavigationStack { HomeView() }
                .tabItem { Label("Inicio", systemImage: "house.fill") }.tag(AppTab.home)
            NavigationStack { CatalogView() }
                .tabItem { Label("Locales", systemImage: "list.bullet") }.tag(AppTab.explore)
            NavigationStack { ChatView() }
                .tabItem { Label("Beloz AI", systemImage: "paperplane.fill") }.tag(AppTab.assistant)
            NavigationStack { CartView() }
                .tabItem { Label("Carrito", systemImage: "cart") }.badge(store.cartCount > 0 ? store.cartCount : 0).tag(AppTab.cart)
            NavigationStack { AccountView() }
                .tabItem { Label("Cuenta", systemImage: "person.crop.circle") }.tag(AppTab.account)
        }
        .toolbarBackground(.white, for: .tabBar)
        .toolbarBackground(.visible, for: .tabBar)
    }
}
