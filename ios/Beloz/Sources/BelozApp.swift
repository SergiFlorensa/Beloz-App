import SwiftUI

@main
struct BelozApp: App {
    @StateObject private var store = BelozStore()

    var body: some Scene {
        WindowGroup {
            BelozTabs()
                .environmentObject(store)
                .tint(BelozStyle.green)
                .preferredColorScheme(.light)
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
                .tabItem { Label("Buscar", systemImage: "magnifyingglass") }.tag(AppTab.explore)
            NavigationStack { ChatView() }
                .tabItem { Label("Beloz AI", systemImage: "sparkles") }.tag(AppTab.assistant)
            NavigationStack { CartView() }
                .tabItem { Label("Pedido", systemImage: "bag") }.badge(store.cartCount > 0 ? store.cartCount : 0).tag(AppTab.cart)
            NavigationStack { AccountView() }
                .tabItem { Label("Cuenta", systemImage: "person.crop.circle") }.tag(AppTab.account)
        }
        .toolbarBackground(.white, for: .tabBar)
        .toolbarBackground(.visible, for: .tabBar)
    }
}
