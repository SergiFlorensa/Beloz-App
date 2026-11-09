import Foundation

private final class NoRedirects: NSObject, URLSessionTaskDelegate {
    func urlSession(_ session: URLSession, task: URLSessionTask,
                    willPerformHTTPRedirection response: HTTPURLResponse,
                    newRequest request: URLRequest,
                    completionHandler: @escaping (URLRequest?) -> Void) {
        completionHandler(nil)
    }
}

actor BelozAPI {
    static let shared = BelozAPI()
    private let baseURL: URL
    private let session: URLSession
    private let encoder = JSONEncoder()
    private let decoder = JSONDecoder()

    private init() {
        let configured = Bundle.main.object(forInfoDictionaryKey: "BELOZ_API_BASE_URL") as? String
        let value = (configured?.isEmpty == false ? configured! : "https://beloz.onrender.com/")
        guard let url = URL(string: value), let scheme = url.scheme?.lowercased(),
              url.host != nil, url.user == nil, url.password == nil,
              url.query == nil, url.fragment == nil, scheme == "https" else {
            fatalError("BELOZ_API_BASE_URL debe ser una URL HTTPS válida.")
        }
        baseURL = url.appending(path: "")
        let configuration = URLSessionConfiguration.ephemeral
        configuration.urlCache = nil
        configuration.httpCookieStorage = nil
        configuration.httpShouldSetCookies = false
        configuration.timeoutIntervalForRequest = 90
        configuration.timeoutIntervalForResource = 120
        session = URLSession(configuration: configuration, delegate: NoRedirects(), delegateQueue: nil)
    }

    nonisolated static func imageURL(_ path: String?) -> URL? {
        guard let path = path?.trimmingCharacters(in: .whitespacesAndNewlines), !path.isEmpty else { return nil }
        if let absolute = URL(string: path), absolute.scheme != nil {
            guard absolute.scheme?.lowercased() == "https", absolute.host != nil,
                  absolute.user == nil, absolute.password == nil else { return nil }
            return absolute
        }
        var clean = path.trimmingCharacters(in: CharacterSet(charactersIn: "/"))
            .replacingOccurrences(of: "^images/", with: "", options: .regularExpression)
        guard !clean.contains(".."), !clean.contains("\\"), !clean.contains(":") else { return nil }
        if clean == "poke_vegano.jpg" || clean == "poke_camaron.jpg" { clean += ".png" }
        let base = Bundle.main.object(forInfoDictionaryKey: "BELOZ_API_BASE_URL") as? String
        let api = URL(string: base?.isEmpty == false ? base! : "https://beloz.onrender.com/")
        return api?.appending(path: "images/\(clean)")
    }

    func restaurants() async throws -> [Restaurant] {
        try await get("api/restaurantes")
    }

    func dishes(restaurantId: Int) async throws -> [Dish] {
        try await get("api/restaurantes/\(restaurantId)/platos")
    }

    func chat(message: String, history: [String]) async throws -> ChatReply {
        try await post("api/chat", body: ChatRequest(message: message, history: history))
    }

    func login(email: String, password: String) async throws -> UserSession {
        let response: AuthResponse = try await post("api/auth/login", body: ["email": email, "password": password], includeStoredSession: false)
        guard let id = response.id, id > 0, let token = response.token, !token.isEmpty else {
            throw APIError.message("El servidor no ha devuelto una sesión válida.")
        }
        let user = UserSession(id: id, email: response.email ?? email, name: response.name ?? "",
                               surname: response.surname ?? "", phone: response.phone ?? "", token: token)
        try SecureSessionStore.save(user)
        return user
    }

    func register(name: String, surname: String, email: String, password: String, phone: String) async throws {
        let _: AuthResponse = try await post("api/auth/register", body: [
            "name": name, "surname": surname, "email": email, "password": password, "num_telefono": phone
        ], includeStoredSession: false)
    }

    func updateEmail(_ email: String) async throws -> UserSession {
        let response: AuthResponse = try await post("api/auth/update_email", body: ["new_email": email], authenticated: true)
        guard var user = SecureSessionStore.read(), response.id == nil || response.id == user.id else {
            throw APIError.message("No se ha podido confirmar el cambio de correo.")
        }
        user.email = response.email ?? email
        try SecureSessionStore.save(user)
        return user
    }

    func updatePhone(_ phone: String) async throws -> UserSession {
        let response: AuthResponse = try await post("api/auth/update_phone", body: ["num_telefono": phone], authenticated: true)
        guard var user = SecureSessionStore.read(), response.id == nil || response.id == user.id else {
            throw APIError.message("No se ha podido confirmar el cambio de teléfono.")
        }
        user.phone = response.phone ?? phone
        try SecureSessionStore.save(user)
        return user
    }

    func updatePassword(current: String, new: String) async throws {
        let _: EmptyResponse = try await post("api/auth/update_password", body: [
            "current_password": current, "new_password": new
        ], authenticated: true)
    }

    func logout() async {
        do { let _: EmptyResponse = try await post("api/auth/logout", body: EmptyBody(), authenticated: true) }
        catch { /* Local credentials are cleared even when the server is unavailable. */ }
        SecureSessionStore.clear()
    }

    func deleteAccount() async throws {
        let _: EmptyResponse = try await post("api/auth/delete_account", body: EmptyBody(), authenticated: true)
        SecureSessionStore.clear()
    }

    func orders(userId: Int) async throws -> [Order] {
        guard var components = URLComponents(url: endpoint("api/pedidos"), resolvingAgainstBaseURL: false) else {
            throw APIError.message("No se pudo preparar la consulta de pedidos.")
        }
        components.queryItems = [URLQueryItem(name: "user_id", value: String(userId))]
        return try await send(components.url!, method: "GET", authenticated: true)
    }

    func createOrder(_ order: CreateOrderRequest) async throws -> Order {
        try await post("api/pedidos/crear", body: order, authenticated: true)
    }

    private func get<T: Decodable>(_ path: String) async throws -> T {
        try await send(endpoint(path), method: "GET", authenticated: false)
    }

    private func post<T: Decodable, Body: Encodable>(_ path: String, body: Body,
                                                       authenticated: Bool = false,
                                                       includeStoredSession: Bool = true) async throws -> T {
        try await send(endpoint(path), method: "POST", body: try encoder.encode(body),
                       authenticated: authenticated, includeStoredSession: includeStoredSession)
    }

    private func send<T: Decodable>(_ url: URL, method: String, body: Data? = nil,
                                    authenticated: Bool, includeStoredSession: Bool = true) async throws -> T {
        guard url.scheme == baseURL.scheme, url.host == baseURL.host, url.port == baseURL.port else {
            throw APIError.message("Destino de red no permitido.")
        }
        var request = URLRequest(url: url, timeoutInterval: 90)
        request.httpMethod = method
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        if body != nil { request.setValue("application/json", forHTTPHeaderField: "Content-Type") }
        request.httpBody = body
        let token = SecureSessionStore.read()?.token
        if authenticated {
            guard let token, !token.isEmpty else {
                throw APIError.message("Inicia sesión para continuar.")
            }
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        } else if includeStoredSession, let token, !token.isEmpty {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        let (data, response) = try await session.data(for: request)
        guard let response = response as? HTTPURLResponse else { throw APIError.message("Respuesta de red no válida.") }
        if (200..<300).contains(response.statusCode) {
            if data.isEmpty, let empty = EmptyResponse() as? T { return empty }
            do { return try decoder.decode(T.self, from: data) }
            catch { throw APIError.message("La respuesta del servicio no tiene el formato esperado.") }
        }
        if response.statusCode == 401, request.value(forHTTPHeaderField: "Authorization") != nil {
            SecureSessionStore.clear()
        }
        throw APIError.message(Self.serverMessage(data) ?? "No se pudo completar la operación (\(response.statusCode)).")
    }

    private func endpoint(_ path: String) -> URL {
        path.split(separator: "/").reduce(baseURL) { $0.appending(path: String($1)) }
    }

    private static func serverMessage(_ data: Data) -> String? {
        guard let json = try? JSONSerialization.jsonObject(with: data) as? [String: Any] else { return nil }
        for key in ["detail", "message", "error"] {
            if let value = json[key] as? String, !value.isEmpty { return value }
        }
        return nil
    }
}

struct EmptyBody: Encodable {}
struct EmptyResponse: Decodable {}

enum APIError: LocalizedError {
    case message(String)
    var errorDescription: String? { if case .message(let text) = self { return text }; return nil }
}
