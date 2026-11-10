import Foundation

enum CatalogOrder: String, CaseIterable, Hashable {
    case relevance, name, wait

    var title: String {
        switch self {
        case .relevance: return "Destacados"
        case .name: return "Nombre A-Z"
        case .wait: return "Espera orientativa"
        }
    }
}

struct CatalogFilter: Hashable {
    var cuisines: Set<String> = []
    var priceTier: Int? = nil
    var order: CatalogOrder = .relevance

    var isActive: Bool { !cuisines.isEmpty || priceTier != nil || order != .relevance }
}

enum DecisionMode: String, CaseIterable, Identifiable {
    case discover, quick, price
    var id: String { rawValue }
    var title: String {
        switch self {
        case .discover: return "Descubrir"
        case .quick: return "Menos espera"
        case .price: return "Precio suave"
        }
    }
}

struct RestaurantChoice: Identifiable {
    let restaurant: Restaurant
    let reason: String
    var id: Int { restaurant.id }
}

enum BelozDiscovery {
    static let cuisines = ["Pizza", "Kebab", "Ramen", "Nachos", "Americana", "Arabe", "Sushi", "India", "Poke", "Peruana", "Hamburguesas"]

    static func normalize(_ value: String) -> String {
        value.folding(options: [.diacriticInsensitive, .caseInsensitive], locale: Locale(identifier: "es_ES"))
            .components(separatedBy: CharacterSet.alphanumerics.inverted)
            .filter { !$0.isEmpty }.joined(separator: " ")
    }

    static func searchRank(name: String, keywords: String, query: String) -> Int? {
        let query = normalize(query)
        guard !query.isEmpty else { return nil }
        let name = normalize(name)
        let words = name.split(separator: " ")
        let tags = normalize(keywords).split(separator: " ")
        let terms = query.split(separator: " ")
        func matches(_ candidates: [Substring]) -> Bool {
            terms.allSatisfy { term in candidates.contains { $0.hasPrefix(term) } }
        }
        if name == query { return 0 }
        if name.hasPrefix(query) { return 1 }
        if matches(words) { return 2 }
        if query.count > 1 && matches(tags) { return 3 }
        if query.count > 1 && matches(words + tags) { return 4 }
        return nil
    }

    static func priceTier(_ value: String) -> Int? {
        switch value.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() {
        case "barato", "bajo", "economico", "económico", "€", "$": return 1
        case "medio", "moderado", "€€", "$$": return 2
        case "caro", "alto", "€€€", "$$$": return 3
        default: return nil
        }
    }

    static func priceLabel(_ value: String) -> String {
        guard let tier = priceTier(value) else { return "Sin dato" }
        return String(repeating: "€", count: tier)
    }

    static func results(_ catalog: [Restaurant], query: String, filter: CatalogFilter) -> [Restaurant] {
        var items = catalog.filter { restaurant in
            (filter.cuisines.isEmpty || filter.cuisines.contains {
                searchRank(name: restaurant.foodType, keywords: "", query: $0) != nil
            }) && (filter.priceTier == nil || priceTier(restaurant.priceLevel) == filter.priceTier)
        }
        if !normalize(query).isEmpty {
            items = items.compactMap { item -> (Restaurant, Int)? in
                guard let rank = searchRank(name: item.name, keywords: item.foodType, query: query) else { return nil }
                return (item, rank)
            }.sorted {
                if $0.1 != $1.1 { return $0.1 < $1.1 }
                let left = normalize($0.0.name), right = normalize($1.0.name)
                return left == right ? $0.0.id < $1.0.id : left < right
            }.map { $0.0 }
        }
        switch filter.order {
        case .relevance: break
        case .name:
            items.sort { normalize($0.name) < normalize($1.name) }
        case .wait:
            items.sort {
                let left = $0.waitTime > 0 ? $0.waitTime : Int.max
                let right = $1.waitTime > 0 ? $1.waitTime : Int.max
                return left == right ? normalize($0.name) < normalize($1.name) : left < right
            }
        }
        return items
    }

    static func choices(_ catalog: [Restaurant], mode: DecisionMode) -> [RestaurantChoice] {
        var seen = Set<Int>()
        let unique = catalog.filter { seen.insert($0.id).inserted }
        let ordered: [Restaurant]
        switch mode {
        case .quick:
            ordered = unique.filter { $0.waitTime > 0 }.sorted {
                $0.waitTime == $1.waitTime ? normalize($0.name) < normalize($1.name) : $0.waitTime < $1.waitTime
            }
        case .price:
            ordered = unique.filter { priceTier($0.priceLevel) != nil }.sorted {
                let left = priceTier($0.priceLevel) ?? Int.max, right = priceTier($1.priceLevel) ?? Int.max
                if left != right { return left < right }
                let leftWait = $0.waitTime > 0 ? $0.waitTime : Int.max
                let rightWait = $1.waitTime > 0 ? $1.waitTime : Int.max
                return leftWait == rightWait ? normalize($0.name) < normalize($1.name) : leftWait < rightWait
            }
        case .discover:
            let alphabetical = unique.sorted { normalize($0.name) < normalize($1.name) }
            var cuisines = Set<String>()
            let varied = alphabetical.filter { cuisines.insert(normalize($0.foodType)).inserted }
            var ids = Set<Int>()
            ordered = (varied + alphabetical).filter { ids.insert($0.id).inserted }
        }
        return ordered.prefix(3).map { restaurant in
            let reason: String
            switch mode {
            case .discover: reason = restaurant.foodType.isEmpty ? "Otra opción de Reus" : restaurant.foodType
            case .quick: reason = "De las esperas más cortas del catálogo"
            case .price: reason = "Nivel de precio orientativo: \(priceLabel(restaurant.priceLevel))"
            }
            return RestaurantChoice(restaurant: restaurant, reason: reason)
        }
    }
}
