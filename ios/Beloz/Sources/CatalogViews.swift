import SwiftUI

struct CatalogView: View {
    @EnvironmentObject private var store: BelozStore
    @State private var query = ""
    @State private var selectedCuisine = "Todas"
    @State private var selectedPrice = "Todos"
    @State private var sortByWait = false

    private var cuisines: [String] {
        Array(Set(store.restaurants.map(\.foodType).filter { !$0.isEmpty })).sorted()
    }

    private var results: [Restaurant] {
        var items = store.restaurants
        if selectedCuisine != "Todas" { items = items.filter { $0.foodType == selectedCuisine } }
        if selectedPrice != "Todos" { items = items.filter { normalizedPrice($0.priceLevel) == selectedPrice } }
        let needle = query.trimmingCharacters(in: .whitespacesAndNewlines)
        if !needle.isEmpty {
            let prefix = items.filter { $0.name.range(of: needle, options: [.anchored, .caseInsensitive, .diacriticInsensitive]) != nil }
            let remainder = items.filter { item in
                !prefix.contains(where: { $0.id == item.id }) &&
                    (item.name.range(of: needle, options: [.caseInsensitive, .diacriticInsensitive]) != nil ||
                     item.foodType.range(of: needle, options: [.caseInsensitive, .diacriticInsensitive]) != nil)
            }
            items = prefix + remainder
        }
        if sortByWait { items.sort { ($0.waitTime == 0 ? Int.max : $0.waitTime) < ($1.waitTime == 0 ? Int.max : $1.waitTime) } }
        return items
    }

    var body: some View {
        ScreenBackdrop {
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 18) {
                    SectionTitle(title: "Explorar", subtitle: "Encuentra tu próximo favorito")
                    HStack(spacing: 10) {
                        Image(systemName: "magnifyingglass").foregroundStyle(BelozStyle.green)
                        TextField("Restaurante o tipo de comida", text: $query)
                            .textInputAutocapitalization(.never).autocorrectionDisabled()
                        if !query.isEmpty {
                            Button { query = "" } label: { Image(systemName: "xmark.circle.fill").foregroundStyle(BelozStyle.muted) }
                        }
                    }
                    .padding(.horizontal, 15).frame(height: 50)
                    .background(.white, in: RoundedRectangle(cornerRadius: 15))
                    .overlay(RoundedRectangle(cornerRadius: 15).stroke(BelozStyle.line, lineWidth: 1))

                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            FilterChip(title: "Todas", selected: selectedCuisine == "Todas") { selectedCuisine = "Todas" }
                            ForEach(cuisines, id: \.self) { cuisine in
                                FilterChip(title: cuisine, selected: selectedCuisine == cuisine) { selectedCuisine = cuisine }
                            }
                        }
                    }
                    ScrollView(.horizontal, showsIndicators: false) {
                        HStack(spacing: 8) {
                            ForEach(["Todos", "€", "€€", "€€€"], id: \.self) { price in
                                FilterChip(title: price, selected: selectedPrice == price) { selectedPrice = price }
                            }
                            FilterChip(title: sortByWait ? "Menos espera ✓" : "Menos espera", selected: sortByWait) { sortByWait.toggle() }
                        }
                    }
                    HStack {
                        Text("\(results.count) restaurantes").font(.subheadline.weight(.semibold)).foregroundStyle(BelozStyle.muted)
                        Spacer()
                        if selectedCuisine != "Todas" || selectedPrice != "Todos" || sortByWait {
                            Button("Limpiar filtros") {
                                selectedCuisine = "Todas"; selectedPrice = "Todos"; sortByWait = false
                            }.font(.caption.weight(.semibold))
                        }
                    }

                    if store.isLoadingRestaurants && store.restaurants.isEmpty {
                        ProgressView("Cargando restaurantes…").frame(maxWidth: .infinity).padding(36)
                    } else if results.isEmpty {
                        ContentUnavailableView("Sin resultados", systemImage: "magnifyingglass",
                            description: Text("Prueba con otro nombre o cambia los filtros."))
                            .frame(maxWidth: .infinity).padding(.vertical, 28)
                    } else {
                        LazyVStack(spacing: 11) {
                            ForEach(results) { restaurant in
                                NavigationLink(destination: RestaurantDetailView(restaurant: restaurant)) {
                                    RestaurantRow(restaurant: restaurant)
                                }.buttonStyle(.plain)
                            }
                        }
                    }
                }
                .padding(.horizontal, 18).padding(.vertical, 18)
            }
        }
        .navigationTitle("Buscar")
        .navigationBarTitleDisplayMode(.inline)
        .task { if store.restaurants.isEmpty { await store.loadRestaurants() } }
        .refreshable { await store.loadRestaurants() }
    }

    private func normalizedPrice(_ value: String) -> String {
        let value = value.lowercased()
        if value.contains("€€€") || value.contains("caro") || value.contains("alto") { return "€€€" }
        if value.contains("€€") || value.contains("medio") || value.contains("moderado") { return "€€" }
        if value.contains("€") || value.contains("bajo") || value.contains("barato") || value.contains("econom") { return "€" }
        return ""
    }
}

struct RestaurantDetailView: View {
    @EnvironmentObject private var store: BelozStore
    let restaurant: Restaurant
    @State private var dishes: [Dish] = []
    @State private var isLoading = true
    @State private var error: String?
    @State private var showChat = false

    private var categories: [String] {
        let found = Set(dishes.map { $0.category.isEmpty ? "Carta" : $0.category })
        return found.sorted { lhs, rhs in
            if lhs == "Carta" { return false }
            if rhs == "Carta" { return true }
            return lhs.localizedCaseInsensitiveCompare(rhs) == .orderedAscending
        }
    }

    var body: some View {
        ScreenBackdrop {
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 20) {
                    RemoteFoodImage(path: restaurant.imagePath, height: 220)
                        .clipShape(RoundedRectangle(cornerRadius: 23))
                        .overlay(alignment: .bottomLeading) {
                            VStack(alignment: .leading, spacing: 5) {
                                Text(restaurant.name).font(.system(size: 24, weight: .bold, design: .rounded)).foregroundStyle(.white)
                                Text(restaurant.foodType).font(.subheadline.weight(.medium)).foregroundStyle(.white.opacity(0.88))
                            }
                            .padding(17)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(LinearGradient(colors: [.clear, .black.opacity(0.67)], startPoint: .top, endPoint: .bottom), in: RoundedRectangle(cornerRadius: 23))
                        }
                    HStack(spacing: 14) {
                        if restaurant.rating > 0 { Label(String(format: "%.1f", restaurant.rating), systemImage: "star.fill") }
                        if restaurant.waitTime > 0 { Label("\(restaurant.waitTime) min", systemImage: "clock") }
                        if !restaurant.priceLevel.isEmpty { Text(restaurant.priceLevel) }
                    }.font(.subheadline.weight(.semibold)).foregroundStyle(BelozStyle.ink)

                    Button { showChat = true } label: {
                        Label("Pregunta por la carta", systemImage: "sparkles")
                            .font(.subheadline.weight(.semibold)).foregroundStyle(BelozStyle.forest)
                            .frame(maxWidth: .infinity, minHeight: 48)
                            .background(BelozStyle.mint, in: RoundedRectangle(cornerRadius: 14))
                    }
                    .buttonStyle(.plain)
                    .navigationDestination(isPresented: $showChat) { ChatView(initialPrompt: "¿Qué me recomiendas de \(restaurant.name)?") }

                    if isLoading {
                        ProgressView("Cargando carta…").frame(maxWidth: .infinity).padding(28)
                    } else if let error {
                        InlineNotice(text: error, isError: true)
                        Button("Reintentar") { Task { await loadMenu() } }.buttonStyle(PrimaryButtonStyle())
                    } else if dishes.isEmpty {
                        InlineNotice(text: "Este restaurante todavía no tiene platos publicados en el catálogo.")
                    } else {
                        ForEach(categories, id: \.self) { category in
                            VStack(alignment: .leading, spacing: 9) {
                                SectionTitle(title: category)
                                VStack(spacing: 0) {
                                    ForEach(dishes.filter { ($0.category.isEmpty ? "Carta" : $0.category) == category }) { dish in
                                        DishRow(dish: dish)
                                        if dish.id != dishes.filter({ ($0.category.isEmpty ? "Carta" : $0.category) == category }).last?.id {
                                            Divider().overlay(BelozStyle.line)
                                        }
                                    }
                                }
                                .padding(.horizontal, 13)
                                .background(.white, in: RoundedRectangle(cornerRadius: 18))
                                .overlay(RoundedRectangle(cornerRadius: 18).stroke(BelozStyle.line, lineWidth: 1))
                            }
                        }
                    }
                }
                .padding(18).padding(.bottom, 28)
            }
        }
        .navigationTitle(restaurant.name)
        .navigationBarTitleDisplayMode(.inline)
        .task { await loadMenu() }
    }

    private func loadMenu() async {
        isLoading = true; error = nil
        defer { isLoading = false }
        do { dishes = try await store.loadDishes(for: restaurant) }
        catch { self.error = error.localizedDescription }
    }
}
