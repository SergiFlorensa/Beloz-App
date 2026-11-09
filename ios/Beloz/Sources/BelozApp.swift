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

struct HomeView: View {
    @EnvironmentObject private var store: BelozStore
    @State private var showChat = false

    private var featuredRestaurants: [Restaurant] {
        let popular = store.restaurants.filter(\.isPopular)
        return Array((popular.isEmpty ? store.restaurants : popular).prefix(6))
    }

    var body: some View {
        ScreenBackdrop {
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 24) {
                    HStack(alignment: .center, spacing: 10) {
                        VStack(alignment: .leading, spacing: 2) {
                            Eyebrow(text: "Reus · Para llevar")
                            Text("Beloz").font(.system(size: 30, weight: .heavy, design: .rounded)).foregroundStyle(BelozStyle.ink)
                        }
                        Spacer()
                        NavigationLink(destination: AccountView()) {
                            Image(systemName: store.session == nil ? "person.crop.circle" : "person.crop.circle.fill")
                                .font(.system(size: 26)).foregroundStyle(BelozStyle.forest)
                        }
                        .accessibilityLabel("Cuenta")
                    }
                    .padding(.top, 12)

                    Button { showChat = true } label: {
                        ZStack(alignment: .leading) {
                            RoundedRectangle(cornerRadius: 24).fill(BelozStyle.forest)
                            HStack(spacing: 6) {
                                VStack(alignment: .leading, spacing: 9) {
                                    HStack(spacing: 6) {
                                        Image(systemName: "sparkles").foregroundStyle(BelozStyle.lime)
                                        Text("Tu antojo, resuelto").font(.system(size: 19, weight: .bold, design: .rounded)).foregroundStyle(.white)
                                    }
                                    Text("Dinos presupuesto, prisa o qué te apetece.")
                                        .font(.subheadline).foregroundStyle(.white.opacity(0.78)).multilineTextAlignment(.leading)
                                    Text("Preguntar a Beloz AI  →")
                                        .font(.system(size: 13, weight: .bold)).foregroundStyle(BelozStyle.lime).padding(.top, 3)
                                }
                                Spacer(minLength: 0)
                                Image("BelozCourier")
                                    .resizable().scaledToFit().frame(width: 104, height: 128)
                                    .accessibilityHidden(true)
                            }
                            .padding(.leading, 20).padding(.trailing, 10)
                        }
                        .frame(height: 158)
                        .clipShape(RoundedRectangle(cornerRadius: 24))
                    }
                    .buttonStyle(.plain)
                    .navigationDestination(isPresented: $showChat) { ChatView() }

                    if let error = store.restaurantError {
                        InlineNotice(text: "No se pudo actualizar el catálogo. \(error)", isError: true)
                    }

                    HStack(alignment: .lastTextBaseline) {
                        SectionTitle(title: "Lo más pedido", subtitle: "Restaurantes de tu zona")
                        Spacer()
                        NavigationLink("Ver todos", destination: CatalogView())
                            .font(.subheadline.weight(.semibold)).foregroundStyle(BelozStyle.green)
                    }

                    if store.isLoadingRestaurants && store.restaurants.isEmpty {
                        ProgressView("Cargando restaurantes…").frame(maxWidth: .infinity).padding(36)
                    } else if store.restaurants.isEmpty {
                        EmptyCatalogState { Task { await store.loadRestaurants() } }
                    } else {
                        VStack(spacing: 11) {
                            ForEach(featuredRestaurants) { restaurant in
                                NavigationLink(destination: RestaurantDetailView(restaurant: restaurant)) {
                                    RestaurantRow(restaurant: restaurant)
                                }
                                .buttonStyle(.plain)
                            }
                        }
                    }
                    Text("Cartas y precios servidos por cada restaurante")
                        .font(.caption).foregroundStyle(BelozStyle.muted)
                        .frame(maxWidth: .infinity, alignment: .center).padding(.top, 2)
                }
                .padding(.horizontal, 18).padding(.bottom, 22)
            }
        }
        .toolbar(.hidden, for: .navigationBar)
        .task { if store.restaurants.isEmpty { await store.loadRestaurants() } }
    }
}

struct EmptyCatalogState: View {
    var retry: () -> Void
    var body: some View {
        VStack(spacing: 12) {
            Image(systemName: "fork.knife.circle").font(.system(size: 34)).foregroundStyle(BelozStyle.green)
            Text("No hay restaurantes disponibles").font(.headline)
            Text("Comprueba la conexión e inténtalo de nuevo.").font(.subheadline).foregroundStyle(BelozStyle.muted)
            Button("Volver a cargar", action: retry).buttonStyle(PrimaryButtonStyle())
        }
        .frame(maxWidth: .infinity).padding(28)
        .background(.white, in: RoundedRectangle(cornerRadius: 20))
    }
}
