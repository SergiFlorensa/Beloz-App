import SwiftUI
import MapKit

private enum HomeDestination: Hashable {
    case catalog(CatalogFilter, String)
    case orders, account, cart, chat(String?), map
}

private enum HomeSheet: String, Identifiable {
    case menu, cuisine, price, sort, comparison
    var id: String { rawValue }
}

struct HomeView: View {
    @EnvironmentObject private var store: BelozStore
    @State private var destination: HomeDestination?
    @State private var pendingDestination: HomeDestination?
    @State private var sheet: HomeSheet?
    @State private var query = ""
    @State private var cuisines: Set<String> = []
    @State private var priceTier: Int?
    @State private var order: CatalogOrder = .relevance
    @State private var mode: DecisionMode = .discover

    private var choices: [RestaurantChoice] { BelozDiscovery.choices(store.restaurants, mode: mode) }
    private var featured: [Restaurant] {
        let popular = store.restaurants.filter(\.isPopular)
        let source = popular.isEmpty ? store.restaurants.sorted { $0.relevance > $1.relevance } : popular
        return Array(source.prefix(6))
    }
    private var searchMatches: [Restaurant] {
        Array(BelozDiscovery.results(store.restaurants, query: query, filter: CatalogFilter()).prefix(5))
    }

    var body: some View {
        ScreenBackdrop {
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 18) {
                    HStack(spacing: 10) {
                        Button { sheet = .menu } label: {
                            Image(systemName: "line.3.horizontal")
                                .font(.system(size: 19, weight: .semibold))
                                .frame(width: 46, height: 46)
                                .background(.white, in: RoundedRectangle(cornerRadius: 8))
                        }
                        .accessibilityLabel("Abrir menú")
                        HStack(spacing: 8) {
                            Image(systemName: "magnifyingglass").foregroundStyle(BelozStyle.green)
                            TextField("Buscar restaurante o comida", text: $query)
                                .textInputAutocapitalization(.never).autocorrectionDisabled()
                                .submitLabel(.search)
                                .onSubmit { destination = .catalog(CatalogFilter(), query) }
                            if !query.isEmpty {
                                Button { query = "" } label: { Image(systemName: "xmark.circle.fill") }
                                    .accessibilityLabel("Borrar búsqueda")
                            }
                        }
                        .padding(.horizontal, 12).frame(height: 46)
                        .background(.white, in: RoundedRectangle(cornerRadius: 8))
                        Button { destination = .map } label: {
                            Image(systemName: "map")
                                .font(.system(size: 18, weight: .semibold)).frame(width: 46, height: 46)
                                .background(.white, in: RoundedRectangle(cornerRadius: 8))
                        }
                        .accessibilityLabel("Abrir mapa de Reus")
                    }
                    .foregroundStyle(BelozStyle.ink)

                    if !BelozDiscovery.normalize(query).isEmpty {
                        VStack(alignment: .leading, spacing: 0) {
                            if searchMatches.isEmpty {
                                Text("Sin coincidencias").font(.subheadline).foregroundStyle(BelozStyle.muted).padding(14)
                            } else {
                                ForEach(searchMatches) { restaurant in
                                    NavigationLink(destination: RestaurantDetailView(restaurant: restaurant)) {
                                        HStack {
                                            VStack(alignment: .leading, spacing: 3) {
                                                Text(restaurant.name).font(.subheadline.weight(.semibold))
                                                Text(restaurant.foodType).font(.caption).foregroundStyle(BelozStyle.muted)
                                            }
                                            Spacer()
                                            Image(systemName: "chevron.right").font(.caption)
                                        }
                                        .padding(12).contentShape(Rectangle())
                                    }.buttonStyle(.plain)
                                    if restaurant.id != searchMatches.last?.id { Divider() }
                                }
                            }
                        }
                        .background(.white, in: RoundedRectangle(cornerRadius: 8))
                    }

                    VStack(alignment: .leading, spacing: 5) {
                        Eyebrow(text: "Beloz / Reus")
                        Text("Hoy, ¿qué te apetece?")
                            .font(.system(size: 29, weight: .bold, design: .rounded))
                            .foregroundStyle(BelozStyle.ink)
                    }

                    HStack(spacing: 8) {
                        homeFilter("Comida", icon: "fork.knife", target: .cuisine)
                        homeFilter("Precio", icon: "eurosign", target: .price)
                        homeFilter("Ordenar", icon: "arrow.up.arrow.down", target: .sort)
                    }

                    Button { destination = .chat(nil) } label: {
                        HStack(spacing: 12) {
                            Image(systemName: "sparkles").font(.title3).foregroundStyle(BelozStyle.lime)
                            VStack(alignment: .leading, spacing: 3) {
                                Text("Beloz AI").font(.headline).foregroundStyle(.white)
                                Text("Encuentra algo para ti").font(.subheadline).foregroundStyle(.white.opacity(0.8))
                            }
                            Spacer()
                            Image(systemName: "arrow.right").foregroundStyle(.white)
                        }
                        .padding(16).frame(maxWidth: .infinity, alignment: .leading)
                        .background(BelozStyle.forest, in: RoundedRectangle(cornerRadius: 8))
                    }.buttonStyle(.plain)

                    Text("Tres opciones. Tú eliges.")
                        .font(.system(size: 21, weight: .bold, design: .rounded))
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(DecisionMode.allCases) { item in
                                FilterChip(title: item.title, selected: mode == item) { mode = item }
                            }
                        }
                    }
                    if store.isLoadingRestaurants && store.restaurants.isEmpty {
                        ProgressView("Cargando restaurantes…").frame(maxWidth: .infinity).padding(24)
                    } else if let error = store.restaurantError, store.restaurants.isEmpty {
                        InlineNotice(text: error, isError: true)
                        Button("Reintentar") { Task { await store.loadRestaurants() } }.buttonStyle(PrimaryButtonStyle())
                    } else if choices.isEmpty {
                        InlineNotice(text: "No hay opciones con estos datos.")
                    } else {
                        ForEach(choices) { choice in
                            NavigationLink(destination: RestaurantDetailView(restaurant: choice.restaurant)) {
                                VStack(alignment: .leading, spacing: 4) {
                                    RestaurantRow(restaurant: choice.restaurant, compact: true)
                                    Text(choice.reason).font(.caption).foregroundStyle(BelozStyle.muted).padding(.horizontal, 4)
                                }
                            }.buttonStyle(.plain)
                        }
                        HStack {
                            Text("Precio y espera orientativos").font(.caption).foregroundStyle(BelozStyle.muted)
                            Spacer()
                            Button("Comparar") { sheet = .comparison }.font(.subheadline.weight(.semibold))
                        }
                    }
                    if !featured.isEmpty {
                        HStack {
                            Text(store.restaurants.contains(where: \.isPopular) ? "Lo más pedido" : "Restaurantes destacados")
                                .font(.system(size: 21, weight: .bold, design: .rounded))
                            Spacer()
                            NavigationLink("Ver todos", destination: CatalogView())
                                .font(.subheadline.weight(.semibold))
                        }
                        ForEach(featured) { restaurant in
                            NavigationLink(destination: RestaurantDetailView(restaurant: restaurant)) {
                                RestaurantRow(restaurant: restaurant, compact: true)
                            }.buttonStyle(.plain)
                        }
                    }
                    Button { destination = .chat("Tengo 12 euros. ¿Qué platos puedo pedir?") } label: {
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("¿Tienes un presupuesto?").font(.headline)
                                Text("Consulta platos con Beloz AI").font(.subheadline).foregroundStyle(BelozStyle.muted)
                            }
                            Spacer()
                            Image(systemName: "arrow.right").foregroundStyle(BelozStyle.green)
                        }.padding(.vertical, 8).contentShape(Rectangle())
                    }.buttonStyle(.plain)
                    NavigationLink("Explorar todos los restaurantes", destination: CatalogView())
                        .font(.subheadline.weight(.semibold)).frame(maxWidth: .infinity).padding(.vertical, 8)
                }
                .padding(.horizontal, 18).padding(.top, 12).padding(.bottom, 24)
            }
            .refreshable { await store.loadRestaurants() }
        }
        .toolbar(.hidden, for: .navigationBar)
        .task { if store.restaurants.isEmpty { await store.loadRestaurants() } }
        .navigationDestination(item: $destination) { target in
            switch target {
            case .catalog(let filter, let query): CatalogView(initialFilter: filter, initialQuery: query)
            case .orders: OrdersView()
            case .account: AccountView()
            case .cart: CartView()
            case .chat(let prompt): ChatView(initialPrompt: prompt)
            case .map: ReusMapView()
            }
        }
        .sheet(item: $sheet, onDismiss: {
            if let pendingDestination { destination = pendingDestination; self.pendingDestination = nil }
        }) { active in
            sheetContent(active)
                .presentationDetents([.medium, .large])
                .presentationDragIndicator(.visible)
        }
    }

    private func homeFilter(_ title: String, icon: String, target: HomeSheet) -> some View {
        Button { sheet = target } label: {
            Label(title, systemImage: icon).font(.system(size: 13, weight: .semibold))
                .lineLimit(1).minimumScaleFactor(0.8)
                .frame(maxWidth: .infinity, minHeight: 42)
                .background(.white, in: RoundedRectangle(cornerRadius: 8))
                .overlay(RoundedRectangle(cornerRadius: 8).stroke(BelozStyle.line, lineWidth: 1))
        }.buttonStyle(.plain)
    }

    private func openAfterSheet(_ target: HomeDestination) {
        pendingDestination = target
        sheet = nil
    }

    @ViewBuilder private func sheetContent(_ active: HomeSheet) -> some View {
        NavigationStack {
            switch active {
            case .menu:
                List {
                    menuRow("Restaurantes", icon: "fork.knife", target: .catalog(CatalogFilter(), ""))
                    menuRow("Mis pedidos", icon: "clock.arrow.circlepath", target: .orders)
                    menuRow("Cuenta y preferencias", icon: "person", target: .account)
                    menuRow("Mi carrito", icon: "bag", target: .cart)
                    menuRow("Beloz AI", icon: "sparkles", target: .chat(nil))
                    menuRow("Mapa de Reus", icon: "map", target: .map)
                }
                .navigationTitle("Tu Beloz").navigationBarTitleDisplayMode(.inline)
            case .cuisine:
                List {
                    ForEach(BelozDiscovery.cuisines, id: \.self) { cuisine in
                        Button {
                            if cuisines.contains(cuisine) { cuisines.remove(cuisine) }
                            else { cuisines.insert(cuisine) }
                        } label: {
                            HStack {
                                Text(cuisine).foregroundStyle(BelozStyle.ink)
                                Spacer()
                                if cuisines.contains(cuisine) { Image(systemName: "checkmark.circle.fill").foregroundStyle(BelozStyle.green) }
                            }.frame(minHeight: 35).contentShape(Rectangle())
                        }
                        .buttonStyle(.plain)
                        .accessibilityAddTraits(cuisines.contains(cuisine) ? .isSelected : [])
                    }
                }
                .navigationTitle("Tipo de comida").navigationBarTitleDisplayMode(.inline)
                .safeAreaInset(edge: .bottom) {
                    Button("Ver restaurantes") {
                        var filter = CatalogFilter(); filter.cuisines = cuisines
                        openAfterSheet(.catalog(filter, ""))
                    }.buttonStyle(PrimaryButtonStyle()).disabled(cuisines.isEmpty).padding()
                }
            case .price:
                List {
                    ForEach(1...3, id: \.self) { tier in
                        Button {
                            priceTier = tier
                        } label: {
                            HStack {
                                Text(["Barato", "Medio", "Caro"][tier - 1]).foregroundStyle(BelozStyle.ink)
                                Spacer()
                                Text(String(repeating: "€", count: tier)).foregroundStyle(BelozStyle.muted)
                                if priceTier == tier { Image(systemName: "checkmark.circle.fill").foregroundStyle(BelozStyle.green) }
                            }.frame(minHeight: 35).contentShape(Rectangle())
                        }.buttonStyle(.plain)
                    }
                }
                .navigationTitle("Precio orientativo").navigationBarTitleDisplayMode(.inline)
                .safeAreaInset(edge: .bottom) {
                    Button("Ver restaurantes") {
                        var filter = CatalogFilter(); filter.priceTier = priceTier
                        openAfterSheet(.catalog(filter, ""))
                    }.buttonStyle(PrimaryButtonStyle()).disabled(priceTier == nil).padding()
                }
            case .sort:
                List {
                    ForEach([CatalogOrder.name, .wait], id: \.self) { value in
                        Button {
                            order = value
                        } label: {
                            HStack {
                                Text(value.title).foregroundStyle(BelozStyle.ink)
                                Spacer()
                                if order == value { Image(systemName: "checkmark.circle.fill").foregroundStyle(BelozStyle.green) }
                            }.frame(minHeight: 35).contentShape(Rectangle())
                        }.buttonStyle(.plain)
                    }
                }
                .navigationTitle("Ordenar restaurantes").navigationBarTitleDisplayMode(.inline)
                .safeAreaInset(edge: .bottom) {
                    Button("Ver restaurantes") {
                        var filter = CatalogFilter(); filter.order = order
                        openAfterSheet(.catalog(filter, ""))
                    }.buttonStyle(PrimaryButtonStyle()).disabled(order == .relevance).padding()
                }
            case .comparison:
                List {
                    ForEach(choices) { choice in
                        VStack(alignment: .leading, spacing: 5) {
                            Text(choice.restaurant.name).font(.headline)
                            Text(choice.restaurant.foodType).font(.subheadline).foregroundStyle(BelozStyle.muted)
                            Text("Precio: \(BelozDiscovery.priceLabel(choice.restaurant.priceLevel)) · \(choice.restaurant.waitTime > 0 ? "~\(choice.restaurant.waitTime) min" : "Espera sin dato")")
                                .font(.caption).foregroundStyle(BelozStyle.muted)
                            NavigationLink("Ver carta", destination: RestaurantDetailView(restaurant: choice.restaurant))
                        }.padding(.vertical, 6)
                    }
                    Text("Estimaciones del catálogo; confirma las condiciones con el restaurante.")
                        .font(.footnote).foregroundStyle(BelozStyle.muted)
                }
                .navigationTitle("Comparar opciones").navigationBarTitleDisplayMode(.inline)
            }
        }
    }

    private func menuRow(_ title: String, icon: String, target: HomeDestination) -> some View {
        Button { openAfterSheet(target) } label: {
            Label(title, systemImage: icon).foregroundStyle(BelozStyle.ink).frame(minHeight: 44)
        }
    }
}

struct ReusMapView: View {
    private static let center = CLLocationCoordinate2D(latitude: 41.1496, longitude: 1.1069)
    @State private var camera: MapCameraPosition = .region(MKCoordinateRegion(
        center: ReusMapView.center, span: MKCoordinateSpan(latitudeDelta: 0.08, longitudeDelta: 0.08)))

    var body: some View {
        Map(position: $camera) { Marker("Reus", coordinate: Self.center) }
            .navigationTitle("Mapa de Reus")
            .navigationBarTitleDisplayMode(.inline)
    }
}
