import Foundation

struct Restaurant: Codable, Hashable, Identifiable {
    let id: Int
    let name: String
    let imagePath: String?
    let waitTime: Int
    let priceLevel: String
    let foodType: String
    let country: String
    let isPopular: Bool
    let rating: Double
    let relevance: Int

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: AnyCodingKey.self)
        id = values.first(Int.self, "restaurante_id", "restaurant_id", "id") ?? 0
        name = values.first(String.self, "name", "nombre") ?? "Restaurante"
        imagePath = values.first(String.self, "image_path", "imagePath")
        waitTime = values.first(Int.self, "wait_time", "waitTime") ?? 0
        priceLevel = values.first(String.self, "price_level", "priceLevel") ?? ""
        foodType = values.first(String.self, "type_of_food", "typeOfFood", "tipo_comida") ?? ""
        country = values.first(String.self, "country", "pais") ?? ""
        isPopular = values.first(Bool.self, "es_popular", "is_popular", "isPopular") ?? false
        rating = values.firstDouble("valoracion", "rating") ?? 0
        relevance = values.first(Int.self, "relevancia", "relevance") ?? 0
    }
}

struct Dish: Codable, Hashable, Identifiable {
    let id: Int
    let name: String
    let details: String
    let price: Double
    let imagePath: String?
    let restaurantId: Int
    let category: String

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: AnyCodingKey.self)
        id = values.first(Int.self, "id", "plato_id") ?? 0
        name = values.first(String.self, "name", "nombre") ?? "Plato"
        details = values.first(String.self, "description", "descripcion") ?? ""
        price = values.firstDouble("price", "precio") ?? 0
        imagePath = values.first(String.self, "image_path", "imagePath")
        restaurantId = values.first(Int.self, "restaurante_id", "restaurant_id", "restaurantId") ?? 0
        category = values.first(String.self, "category", "categoria")?.trimmingCharacters(in: .whitespacesAndNewlines)
            .nonEmpty ?? "Carta"
    }
}

struct UserSession: Codable, Equatable {
    let id: Int
    var email: String
    var name: String
    var surname: String
    var phone: String
    var token: String

    var displayName: String { "\(name) \(surname)".trimmingCharacters(in: .whitespacesAndNewlines) }
}

struct AuthResponse: Decodable {
    let id: Int?
    let email: String?
    let name: String?
    let surname: String?
    let phone: String?
    let token: String?

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: AnyCodingKey.self)
        id = values.first(Int.self, "id_user", "user_id", "id")
        email = values.first(String.self, "email")
        name = values.first(String.self, "name", "nombre")
        surname = values.first(String.self, "surname", "apellido")
        phone = values.first(String.self, "num_telefono", "phone")
        token = values.first(String.self, "token", "access_token")
    }
}

struct ChatRequest: Encodable {
    let message: String
    let history: [String]
    let profile: TasteProfile?

    enum CodingKeys: String, CodingKey {
        case message, history
        case profile = "perfil_sabor"
    }
}

struct TasteProfile: Encodable {
    struct Entry: Encodable {
        let clave: String
        let conteo: Int
    }
    let topTypes: [Entry]

    enum CodingKeys: String, CodingKey {
        case topTypes = "top_tipos_comida"
    }
}

struct ChatReply: Decodable {
    let provider: String?
    let response: String
    let suggestions: [ChatSuggestion]
    let sources: [ChatSource]

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: AnyCodingKey.self)
        provider = values.first(String.self, "provider")
        response = values.first(String.self, "respuesta", "response", "answer") ?? "No he podido preparar una respuesta."
        suggestions = values.first([ChatSuggestion].self, "sugerencias", "suggestions") ?? []
        sources = values.first([ChatSource].self, "sources", "fuentes") ?? []
    }
}

struct ChatSuggestion: Decodable, Identifiable {
    let restaurantId: Int
    let restaurantName: String
    let imagePath: String?
    let dishName: String?
    let price: Double?
    let waitTime: Int?
    let foodType: String?
    let reason: String?

    var id: String { "\(restaurantId)-\(dishName ?? restaurantName)" }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: AnyCodingKey.self)
        restaurantId = values.first(Int.self, "restaurante_id", "restaurant_id") ?? 0
        restaurantName = values.first(String.self, "restaurante_nombre", "restaurant_name") ?? "Restaurante"
        imagePath = values.first(String.self, "image_path", "imagePath")
        dishName = values.first(String.self, "plato", "dish")
        price = values.firstDouble("price", "precio")
        waitTime = values.first(Int.self, "wait_time", "waitTime")
        foodType = values.first(String.self, "type_of_food", "food_type")
        reason = values.first(String.self, "motivo", "reason")
    }
}

struct ChatSource: Decodable, Identifiable {
    let label: String?
    let url: String?
    let updatedAt: String?
    var id: String { url ?? label ?? UUID().uuidString }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: AnyCodingKey.self)
        label = values.first(String.self, "label")
        url = values.first(String.self, "url")
        updatedAt = values.first(String.self, "updated_at", "updatedAt", "retrieved_at")
    }
}

struct Order: Decodable, Identifiable {
    let id: Int
    let userId: Int
    let restaurantId: Int
    let date: String
    let total: Double

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: AnyCodingKey.self)
        id = values.first(Int.self, "id", "pedido_id") ?? 0
        userId = values.first(Int.self, "user_id", "userId") ?? 0
        restaurantId = values.first(Int.self, "restaurant_id", "restaurante_id") ?? 0
        date = values.first(String.self, "fecha", "created_at") ?? ""
        total = values.firstDouble("total") ?? 0
    }
}

struct OrderDetail: Decodable, Identifiable {
    let id: Int
    let name: String
    let restaurantName: String
    let quantity: Int
    let price: Double

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: AnyCodingKey.self)
        id = values.first(Int.self, "id_detalle", "id") ?? 0
        name = values.first(String.self, "plato_nombre", "name") ?? "Plato"
        restaurantName = values.first(String.self, "restaurante_nombre") ?? ""
        quantity = values.first(Int.self, "cantidad", "quantity") ?? 0
        price = values.firstDouble("precio", "price") ?? 0
    }
}

struct OrderLine: Encodable, Decodable, Identifiable {
    let dishId: Int
    let quantity: Int
    let price: Double
    var id: Int { dishId }

    enum CodingKeys: String, CodingKey {
        case dishId = "platoId"
        case quantity = "cantidad"
        case price = "precio"
    }
}

struct CreateOrderRequest: Encodable {
    let userId: Int
    let restaurantId: Int
    let total: Double
    let details: [OrderLine]

    enum CodingKeys: String, CodingKey {
        case userId = "user_id"
        case restaurantId = "restaurant_id"
        case total
        case details = "detalles"
    }
}

struct CartLine: Codable, Identifiable {
    var dish: Dish
    var quantity: Int
    var id: Int { dish.id }
    var lineTotal: Double { dish.price * Double(quantity) }
}

struct AnyCodingKey: CodingKey {
    let stringValue: String
    let intValue: Int?
    init(_ value: String) { stringValue = value; intValue = nil }
    init?(stringValue: String) { self.init(stringValue) }
    init?(intValue: Int) { stringValue = String(intValue); self.intValue = intValue }
}

extension KeyedDecodingContainer where Key == AnyCodingKey {
    func first<T: Decodable>(_ type: T.Type, _ names: String...) -> T? {
        for name in names {
            if let value = try? decode(T.self, forKey: AnyCodingKey(name)) { return value }
        }
        return nil
    }

    func firstDouble(_ names: String...) -> Double? {
        for name in names {
            let key = AnyCodingKey(name)
            if let value = try? decode(Double.self, forKey: key), value.isFinite { return value }
            if let text = try? decode(String.self, forKey: key),
               let value = Double(text.trimmingCharacters(in: .whitespacesAndNewlines)), value.isFinite {
                return value
            }
        }
        return nil
    }
}

extension String {
    var nonEmpty: String? { isEmpty ? nil : self }
}

extension Double {
    var euro: String { self.formatted(.currency(code: "EUR")) }
}
