import Foundation

@main
struct ContractTests {
    static func expect(_ condition: @autoclosure () -> Bool, _ message: String) {
        guard condition() else { fatalError(message) }
    }

    static func main() throws {
        let decoder = JSONDecoder()
        let json = #"""
        [
          {"restaurante_id":1,"name":"La Tagliatella","type_of_food":"Pasta, Pizza","price_level":"€€","wait_time":20,"valoracion":"4.2"},
          {"restaurante_id":2,"name":"Café Central","type_of_food":"Tapas","price_level":"€","wait_time":10},
          {"restaurante_id":3,"name":"Kokoro","type_of_food":"Sushi, Ramen","price_level":"€€€","wait_time":0}
        ]
        """#
        let restaurants = try decoder.decode([Restaurant].self, from: Data(json.utf8))
        expect(restaurants[0].rating == 4.2, "Decimal strings should decode as numbers")
        expect(BelozDiscovery.searchRank(name: "La Tagliatella", keywords: "Pasta", query: "tagli") == 2,
               "Search should match a word prefix")
        expect(BelozDiscovery.results(restaurants, query: "cafe", filter: CatalogFilter()).map(\.id) == [2],
               "Search should ignore accents")
        expect(BelozDiscovery.results(restaurants, query: "sus", filter: CatalogFilter()).map(\.id) == [3],
               "Search should match cuisine prefixes")
        expect(BelozDiscovery.results(restaurants, query: "c", filter: CatalogFilter()).map(\.id) == [2],
               "One character should match restaurant names only")
        var filter = CatalogFilter()
        filter.cuisines = ["Pizza", "Sushi"]
        expect(BelozDiscovery.results(restaurants, query: "", filter: filter).map(\.id) == [1, 3],
               "Cuisine filters should match comma-separated values")
        filter = CatalogFilter(); filter.priceTier = 1
        expect(BelozDiscovery.results(restaurants, query: "", filter: filter).map(\.id) == [2],
               "Price tiers should be filtered accurately")
        filter = CatalogFilter(); filter.order = .wait
        expect(BelozDiscovery.results(restaurants, query: "", filter: filter).map(\.id) == [2, 1, 3],
               "Unknown waiting times should sort last")
        expect(BelozDiscovery.choices(restaurants, mode: .quick).map(\.id) == [2, 1],
               "Quick choices must exclude unknown waiting times")

        let dishJSON = #"{"id":121,"name":"Plato","price":"5.50","restaurante_id":1}"#
        let dish = try decoder.decode(Dish.self, from: Data(dishJSON.utf8))
        expect(dish.price == 5.5, "PostgreSQL numeric strings must not become free")
        let cart = [CartLine(dish: dish, quantity: 2)]
        let restored = try decoder.decode([CartLine].self, from: JSONEncoder().encode(cart))
        expect(restored[0].lineTotal == 11, "Cart persistence must preserve price")
        let detailJSON = #"{"id_detalle":1,"plato_nombre":"Plato","cantidad":2,"precio":"5.50"}"#
        let detail = try decoder.decode(OrderDetail.self, from: Data(detailJSON.utf8))
        expect(detail.price == 5.5 && detail.quantity == 2, "Order details must preserve numeric strings")
        let menuJSON = #"""
        [
          {"id":1,"name":"Uno","price":"5.50","restaurante_id":1,"category":"Entrantes"},
          {"id":2,"name":"Dos","price":6,"restaurante_id":1,"category":"RAMEN"},
          {"id":3,"name":"Tres","price":7,"restaurante_id":1,"category":"entrantes"},
          {"id":3,"name":"Tres duplicado","price":7,"restaurante_id":1,"category":"entrantes"}
        ]
        """#
        let menu = try decoder.decode([Dish].self, from: Data(menuJSON.utf8))
        let sections = BelozDiscovery.menuSections(menu)
        expect(sections.map(\.title) == ["Entrantes", "RAMEN"], "Sections should preserve catalogue order")
        expect(sections[0].dishes.map(\.id) == [1, 3], "Sections should merge labels and remove duplicate dishes")
        let request = ChatRequest(message: "hola", history: [], profile: nil)
        let requestJSON = String(decoding: try JSONEncoder().encode(request), as: UTF8.self)
        expect(!requestJSON.contains("perfil_sabor"), "Taste profile must be absent without consent")
        let suggestionJSON = #"{"restaurante_id":1,"restaurante_nombre":"Restaurante","precio":"6.25","source":{"label":"Carta","url":"https://example.org"}}"#
        let suggestion = try decoder.decode(ChatSuggestion.self, from: Data(suggestionJSON.utf8))
        expect(suggestion.price == 6.25 && suggestion.source?.label == "Carta", "Suggestions should preserve prices and source metadata")
        print("Beloz iOS contract tests passed")
    }
}
