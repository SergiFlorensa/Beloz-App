import Foundation
import SwiftUI

@MainActor
final class BelozStore: ObservableObject {
    @Published private(set) var restaurants: [Restaurant] = []
    @Published private(set) var isLoadingRestaurants = false
    @Published private(set) var restaurantError: String?
    @Published private(set) var session: UserSession? = SecureSessionStore.read()
    @Published private(set) var cart: [CartLine] = []
    @Published private(set) var chatMessages: [ConversationMessage] = [
        ConversationMessage(role: .assistant, text: "Hola. ¿Qué te apetece pedir hoy?")
    ]
    @Published private(set) var isSendingChat = false
    @Published private(set) var chatError: String?
    @Published private(set) var failedChatMessage: String?
    @Published private(set) var orders: [Order] = []
    @Published private(set) var isLoadingOrders = false
    @Published private(set) var isSubmittingOrder = false

    private let api = BelozAPI.shared

    init() {
        if let data = UserDefaults.standard.data(forKey: "beloz.cart.v1"),
           let saved = try? JSONDecoder().decode([CartLine].self, from: data) {
            cart = saved.filter { $0.quantity > 0 }
        }
    }

    var cartCount: Int { cart.reduce(0) { $0 + $1.quantity } }
    var cartTotal: Double { cart.reduce(0) { $0 + $1.lineTotal } }
    var cartRestaurantId: Int? { cart.first?.dish.restaurantId }

    func loadRestaurants() async {
        guard !isLoadingRestaurants else { return }
        isLoadingRestaurants = true
        restaurantError = nil
        defer { isLoadingRestaurants = false }
        do {
            let loaded = try await api.restaurants()
            restaurants = loaded.filter { $0.id > 0 }
        } catch {
            restaurantError = error.localizedDescription
        }
    }

    func loadDishes(for restaurant: Restaurant) async throws -> [Dish] {
        let loaded = try await api.dishes(restaurantId: restaurant.id)
        return loaded.filter { $0.id > 0 }
    }

    @discardableResult
    func addToCart(_ dish: Dish, replacingExisting: Bool = false) -> Bool {
        if let owner = cartRestaurantId, owner != dish.restaurantId {
            guard replacingExisting else { return false }
            cart = []
        }
        if let index = cart.firstIndex(where: { $0.dish.id == dish.id }) {
            cart[index].quantity += 1
        } else {
            cart.append(CartLine(dish: dish, quantity: 1))
        }
        persistCart()
        return true
    }

    func setQuantity(for dishId: Int, to quantity: Int) {
        if quantity <= 0 { cart.removeAll { $0.dish.id == dishId } }
        else if let index = cart.firstIndex(where: { $0.dish.id == dishId }) { cart[index].quantity = quantity }
        persistCart()
    }

    func clearCart() {
        cart = []
        persistCart()
    }

    func sendChat(_ text: String) async {
        let message = text.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !message.isEmpty, !isSendingChat else { return }
        let history = chatMessages.filter { $0.role == .user }.suffix(4).map(\.text)
        chatMessages.append(ConversationMessage(role: .user, text: message))
        await requestChat(message, history: Array(history))
    }

    func retryChat() async {
        guard let message = failedChatMessage, !isSendingChat else { return }
        let history = chatMessages.dropLast().filter { $0.role == .user }.suffix(4).map(\.text)
        await requestChat(message, history: Array(history))
    }

    private func requestChat(_ message: String, history: [String]) async {
        isSendingChat = true
        chatError = nil
        failedChatMessage = nil
        defer { isSendingChat = false }
        do {
            let reply = try await api.chat(message: message, history: history)
            chatMessages.append(ConversationMessage(role: .assistant, text: reply.response,
                provider: reply.provider, suggestions: reply.suggestions, sources: reply.sources))
        } catch {
            chatError = error.localizedDescription
            failedChatMessage = message
        }
    }

    func resetChat() {
        guard !isSendingChat else { return }
        chatMessages = [ConversationMessage(role: .assistant, text: "Hola. ¿Qué te apetece pedir hoy?")]
        chatError = nil
        failedChatMessage = nil
    }

    func login(email: String, password: String) async throws {
        session = try await api.login(email: email, password: password)
        await loadOrders()
    }

    func register(name: String, surname: String, email: String, password: String, phone: String) async throws {
        try await api.register(name: name, surname: surname, email: email, password: password, phone: phone)
    }

    func updateEmail(_ email: String) async throws {
        session = try await api.updateEmail(email)
    }

    func updatePhone(_ phone: String) async throws {
        session = try await api.updatePhone(phone)
    }

    func updatePassword(current: String, new: String) async throws {
        try await api.updatePassword(current: current, new: new)
    }

    func logout() async {
        await api.logout()
        session = nil
        orders = []
    }

    func deleteAccount() async throws {
        try await api.deleteAccount()
        session = nil
        orders = []
    }

    func loadOrders() async {
        guard let user = session else { orders = []; return }
        isLoadingOrders = true
        defer { isLoadingOrders = false }
        do { orders = try await api.orders(userId: user.id).sorted { $0.id > $1.id } }
        catch {
            orders = []
            if SecureSessionStore.read() == nil { session = nil }
        }
    }

    func submitOrder() async throws -> Order {
        guard let user = session else { throw APIError.message("Inicia sesión para confirmar el pedido.") }
        guard let restaurantId = cartRestaurantId, !cart.isEmpty else {
            throw APIError.message("Añade algún plato antes de continuar.")
        }
        isSubmittingOrder = true
        defer { isSubmittingOrder = false }
        let order = try await api.createOrder(CreateOrderRequest(
            userId: user.id,
            restaurantId: restaurantId,
            total: cartTotal,
            details: cart.map { OrderLine(dishId: $0.dish.id, quantity: $0.quantity, price: $0.dish.price) }
        ))
        clearCart()
        await loadOrders()
        return order
    }

    private func persistCart() {
        guard let data = try? JSONEncoder().encode(cart) else { return }
        UserDefaults.standard.set(data, forKey: "beloz.cart.v1")
    }
}

struct ConversationMessage: Identifiable {
    enum Role { case user, assistant }
    let id = UUID()
    let role: Role
    let text: String
    var provider: String? = nil
    var suggestions: [ChatSuggestion] = []
    var sources: [ChatSource] = []
}
