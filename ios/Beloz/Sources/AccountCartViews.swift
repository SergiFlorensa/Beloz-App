import SwiftUI
import UIKit

struct CartView: View {
    @EnvironmentObject private var store: BelozStore
    @State private var showAuth = false
    @State private var alertTitle = ""
    @State private var alertMessage = ""
    @State private var showAlert = false
    @State private var completedOrder: Order?

    var body: some View {
        ScreenBackdrop {
            Group {
                if store.cart.isEmpty {
                    ContentUnavailableView("Tu pedido está vacío", systemImage: "bag",
                        description: Text("Explora restaurantes y añade algo que te apetezca."))
                } else {
                    ScrollView(showsIndicators: false) {
                        VStack(alignment: .leading, spacing: 18) {
                            SectionTitle(title: "Tu pedido", subtitle: store.cartRestaurantName)
                            VStack(spacing: 0) {
                                ForEach(store.cart) { line in
                                    CartLineRow(line: line) { value in store.setQuantity(for: line.dish.id, to: value) }
                                    if line.id != store.cart.last?.id { Divider().overlay(BelozStyle.line) }
                                }
                            }
                            .padding(.horizontal, 14)
                            .background(.white, in: RoundedRectangle(cornerRadius: 19))
                            .overlay(RoundedRectangle(cornerRadius: 19).stroke(BelozStyle.line, lineWidth: 1))

                            HStack {
                                Text("Subtotal").font(.subheadline).foregroundStyle(BelozStyle.muted)
                                Spacer()
                                Text(store.cartTotal.euro).font(.system(size: 20, weight: .bold, design: .rounded))
                            }
                            .padding(.horizontal, 2)
                            if store.cartHasInvalidPrice {
                                InlineNotice(text: "Hay platos sin precio. Elimínalos y vuelve a añadirlos desde la carta.", isError: true)
                            }
                            InlineNotice(text: "Este flujo registra el pedido, pero no realiza un cobro online. Confirma el método de pago con el restaurante.")
                        }
                        .padding(18).padding(.bottom, 12)
                    }
                }
            }
        }
        .navigationTitle("Pedido")
        .navigationBarTitleDisplayMode(.inline)
        .safeAreaInset(edge: .bottom) {
            if !store.cart.isEmpty {
                VStack(spacing: 8) {
                    Button {
                        guard store.session != nil else { showAuth = true; return }
                        Task {
                            do { completedOrder = try await store.submitOrder(); alertTitle = "Pedido registrado"; alertMessage = "Tu pedido #\(completedOrder?.id ?? 0) se ha enviado al servicio Beloz." }
                            catch { alertTitle = "No se pudo enviar"; alertMessage = error.localizedDescription }
                            showAlert = true
                        }
                    } label: {
                        HStack {
                            if store.isSubmittingOrder { ProgressView().tint(BelozStyle.ink) }
                            Text(store.session == nil ? "Iniciar sesión para pedir" : "Enviar pedido")
                            Spacer()
                            Text(store.cartTotal.euro)
                        }
                        .padding(.horizontal, 18).frame(maxWidth: .infinity, minHeight: 52)
                    }
                    .buttonStyle(PrimaryButtonStyle())
                    .disabled(store.isSubmittingOrder || store.cartHasInvalidPrice)
                }
                .padding(.horizontal, 16).padding(.top, 10).padding(.bottom, 4)
                .background(.regularMaterial)
            }
        }
        .sheet(isPresented: $showAuth) { AccountAuthSheet() }
        .alert(alertTitle, isPresented: $showAlert) { Button("Aceptar", role: .cancel) { } } message: { Text(alertMessage) }
    }
}

private extension BelozStore {
    var cartRestaurantName: String? {
        guard let id = cartRestaurantId else { return nil }
        return restaurants.first(where: { $0.id == id })?.name
    }
}

private struct CartLineRow: View {
    let line: CartLine
    var setQuantity: (Int) -> Void

    var body: some View {
        HStack(spacing: 11) {
            VStack(alignment: .leading, spacing: 4) {
                Text(line.dish.name).font(.subheadline.weight(.semibold)).lineLimit(2)
                Text(line.dish.price.isFinite && line.dish.price > 0 ? line.lineTotal.euro : "Precio no disponible")
                    .font(.caption.weight(.semibold)).foregroundStyle(BelozStyle.green)
            }
            Spacer()
            HStack(spacing: 12) {
                Button { setQuantity(line.quantity - 1) } label: { Image(systemName: "minus.circle").font(.title3) }
                Text("\(line.quantity)").font(.subheadline.monospacedDigit().weight(.semibold)).frame(minWidth: 16)
                Button { setQuantity(line.quantity + 1) } label: { Image(systemName: "plus.circle").font(.title3) }
                    .disabled(line.quantity >= 99)
            }
            .foregroundStyle(BelozStyle.forest)
            .buttonStyle(.plain)
        }
        .padding(.vertical, 14)
    }
}

struct AccountView: View {
    @EnvironmentObject private var store: BelozStore
    @State private var showAuth = false
    @State private var startInRegister = false
    @State private var showSettings = false
    @State private var showPassword = false
    @State private var confirmDelete = false
    @State private var alertText: String?

    var body: some View {
        ScreenBackdrop {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Image(systemName: "person.fill").font(.system(size: 34)).foregroundStyle(BelozStyle.green)
                    Text(store.session.map { "Bienvenido/a, \($0.name)" } ?? "A tu gusto.")
                        .font(.system(size: 26, weight: .bold)).foregroundStyle(BelozStyle.ink)

                    if let user = store.session {
                        Text(user.email).font(.subheadline).foregroundStyle(BelozStyle.muted)
                        accountButton("Correo electrónico", icon: "envelope") { showSettings = true }
                        accountButton("Contraseña", icon: "lock") { showPassword = true }
                        accountButton("Número de teléfono", icon: "phone") { showSettings = true }
                        NavigationLink(destination: OrdersView()) {
                            accountRow("Mis pedidos", icon: "clock.arrow.circlepath")
                        }.buttonStyle(.plain)
                    } else {
                        Button("Iniciar sesión") { startInRegister = false; showAuth = true }
                            .buttonStyle(PrimaryButtonStyle()).frame(maxWidth: .infinity, alignment: .leading)
                        Button("Crear cuenta") { startInRegister = true; showAuth = true }
                            .font(.subheadline.weight(.semibold)).foregroundStyle(BelozStyle.green)
                            .frame(minHeight: 44)
                    }

                    Divider().overlay(BelozStyle.line)
                    Text("Tus preferencias").font(.headline)
                    Toggle("Recordar mis gustos", isOn: Binding(
                        get: { store.personalizationEnabled },
                        set: { store.setPersonalization($0) }
                    ))
                    .tint(BelozStyle.green)
                    Text("Guarda los tipos de cocina que visitas en este dispositivo. Si lo activas, el chat recibe un resumen de hasta cinco tipos. Al desactivarlo se borra el historial.")
                        .font(.caption).foregroundStyle(BelozStyle.muted)
                    Button("Borrar historial de gustos") { store.clearTasteHistory() }
                        .font(.subheadline.weight(.semibold)).disabled(!store.personalizationEnabled)

                    Divider().overlay(BelozStyle.line)
                    NavigationLink(destination: PaymentAvailabilityView()) {
                        accountRow("Pagos", icon: "creditcard")
                    }.buttonStyle(.plain)
                    NavigationLink(destination: PrivacySummaryView()) {
                        accountRow("Privacidad", icon: "hand.raised")
                    }.buttonStyle(.plain)
                    if store.session != nil {
                        Button("Cerrar sesión") { Task { await store.logout() } }
                            .font(.subheadline.weight(.semibold)).frame(minHeight: 48)
                        Button("Eliminar mi cuenta", role: .destructive) { confirmDelete = true }
                            .font(.subheadline).frame(minHeight: 44)
                    }
                }
                .padding(24)
            }
        }
        .navigationTitle("Tu cuenta")
        .navigationBarTitleDisplayMode(.inline)
        .sheet(isPresented: $showAuth) { AccountAuthSheet(startInRegister: startInRegister) }
        .sheet(isPresented: $showSettings) { AccountSettingsSheet() }
        .sheet(isPresented: $showPassword) { PasswordChangeSheet() }
        .confirmationDialog("¿Eliminar la cuenta?", isPresented: $confirmDelete, titleVisibility: .visible) {
            Button("Eliminar cuenta", role: .destructive) {
                Task { do { try await store.deleteAccount() } catch { alertText = error.localizedDescription } }
            }
            Button("Cancelar", role: .cancel) { }
        } message: { Text("La operación se enviará al servicio Beloz y no se puede deshacer.") }
        .alert("Cuenta", isPresented: Binding(get: { alertText != nil }, set: { if !$0 { alertText = nil } })) {
            Button("Aceptar", role: .cancel) { alertText = nil }
        } message: { Text(alertText ?? "") }
    }

    private func accountButton(_ title: String, icon: String, action: @escaping () -> Void) -> some View {
        Button(action: action) { accountRow(title, icon: icon) }.buttonStyle(.plain)
    }

    private func accountRow(_ title: String, icon: String) -> some View {
        HStack(spacing: 14) {
            Image(systemName: icon).foregroundStyle(BelozStyle.green).frame(width: 24)
            Text(title).foregroundStyle(BelozStyle.ink)
            Spacer()
            Image(systemName: "arrow.right").foregroundStyle(BelozStyle.green)
        }.frame(maxWidth: .infinity, minHeight: 56).contentShape(Rectangle())
    }
}

private struct PaymentAvailabilityView: View {
    var body: some View {
        ScreenBackdrop {
            ContentUnavailableView("Pago online no disponible", systemImage: "creditcard",
                description: Text("Beloz no almacena tarjetas ni realiza cobros online. Confirma el pago con el restaurante."))
                .padding(18)
        }
        .navigationTitle("Pagos").navigationBarTitleDisplayMode(.inline)
    }
}

private struct PrivacySummaryView: View {
    @EnvironmentObject private var store: BelozStore
    @State private var confirmDelete = false
    @State private var error: String?

    var body: some View {
        ScreenBackdrop {
            List {
                Section {
                    NavigationLink("Gestión de cookies") {
                        PrivacyDetailView(title: "Cookies", message: "Las solicitudes a la API usan una sesión temporal sin almacenamiento de cookies. Las imágenes remotas pueden depender de los servicios que las alojan.")
                    }
                    NavigationLink("Permisos y datos") {
                        PrivacyDetailView(title: "Permisos y datos", message: "La sesión se guarda en el llavero del dispositivo. Los pedidos y mensajes enviados se procesan en el servidor configurado. El mapa muestra Reus y no solicita tu ubicación.")
                    }
                    NavigationLink("Política de privacidad") {
                        PrivacyDetailView(title: "Política de privacidad", message: "La política completa de Beloz está pendiente de publicación. Antes de distribuir la aplicación debe revisarse junto con el tratamiento real de datos del servidor.")
                    }
                    if store.session != nil {
                        Button("Eliminar cuenta", role: .destructive) { confirmDelete = true }
                    }
                }
            }.scrollContentBackground(.hidden)
        }
        .navigationTitle("Privacidad").navigationBarTitleDisplayMode(.inline)
        .confirmationDialog("¿Eliminar la cuenta?", isPresented: $confirmDelete, titleVisibility: .visible) {
            Button("Eliminar cuenta", role: .destructive) {
                Task { do { try await store.deleteAccount() } catch { self.error = error.localizedDescription } }
            }
            Button("Cancelar", role: .cancel) { }
        } message: { Text("La operación se enviará al servicio Beloz y no se puede deshacer.") }
        .alert("Cuenta", isPresented: Binding(get: { error != nil }, set: { if !$0 { error = nil } })) {
            Button("Aceptar", role: .cancel) { error = nil }
        } message: { Text(error ?? "") }
    }
}

private struct PrivacyDetailView: View {
    let title: String
    let message: String
    var body: some View {
        ScreenBackdrop {
            ScrollView {
                Text(message).font(.body).frame(maxWidth: .infinity, alignment: .leading).padding(20)
            }
        }
        .navigationTitle(title).navigationBarTitleDisplayMode(.inline)
    }
}

struct AccountAuthSheet: View {
    @EnvironmentObject private var store: BelozStore
    @Environment(\.dismiss) private var dismiss
    @State private var isRegister: Bool
    @State private var name = ""
    @State private var surname = ""
    @State private var phone = ""
    @State private var email = ""
    @State private var password = ""
    @State private var busy = false
    @State private var error: String?
    @State private var notice: String?

    init(startInRegister: Bool = false) {
        _isRegister = State(initialValue: startInRegister)
    }

    var body: some View {
        NavigationStack {
            ScreenBackdrop {
                ScrollView {
                    VStack(alignment: .leading, spacing: 14) {
                        Image("BelozCourier").resizable().scaledToFit()
                            .frame(maxWidth: .infinity).frame(height: 125)
                            .accessibilityLabel("Beloz")
                        SectionTitle(title: isRegister ? "Crear cuenta" : "Bienvenido/a", subtitle: "Accede a tus pedidos y a tu perfil")
                        if isRegister {
                            input("Nombre", text: $name, icon: "person")
                            input("Apellidos", text: $surname, icon: "person.2")
                            input("Teléfono", text: $phone, icon: "phone", keyboard: .phonePad)
                        }
                        input("Correo electrónico", text: $email, icon: "envelope", keyboard: .emailAddress)
                        VStack(alignment: .leading, spacing: 7) {
                            Text("Contraseña").font(.caption.weight(.semibold)).foregroundStyle(BelozStyle.muted)
                            SecureField("Contraseña", text: $password).textContentType(isRegister ? .newPassword : .password)
                                .padding(14).background(.white, in: RoundedRectangle(cornerRadius: 13))
                                .overlay(RoundedRectangle(cornerRadius: 13).stroke(BelozStyle.line, lineWidth: 1))
                        }
                        if let error { InlineNotice(text: error, isError: true) }
                        if let notice { InlineNotice(text: notice) }
                        Button {
                            Task { await submit() }
                        } label: {
                            HStack {
                                if busy { ProgressView().tint(BelozStyle.ink) }
                                Text(isRegister ? "Crear cuenta" : "Iniciar sesión")
                            }
                            .frame(maxWidth: .infinity, minHeight: 50)
                        }
                        .buttonStyle(PrimaryButtonStyle()).disabled(busy)
                        Button(isRegister ? "Ya tengo una cuenta" : "Crear una cuenta") {
                            isRegister.toggle(); error = nil; notice = nil
                        }
                        .font(.subheadline.weight(.semibold)).frame(maxWidth: .infinity).padding(.vertical, 7)
                    }.padding(20)
                }
            }
            .navigationTitle(isRegister ? "Registro" : "Acceso")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar { ToolbarItem(placement: .topBarTrailing) { Button("Cerrar") { dismiss() } } }
        }
    }

    private func input(_ title: String, text: Binding<String>, icon: String,
                       keyboard: UIKeyboardType = .default) -> some View {
        VStack(alignment: .leading, spacing: 7) {
            Text(title).font(.caption.weight(.semibold)).foregroundStyle(BelozStyle.muted)
            HStack(spacing: 10) {
                Image(systemName: icon).foregroundStyle(BelozStyle.green).frame(width: 20)
                TextField(title, text: text).keyboardType(keyboard)
                    .textInputAutocapitalization(keyboard == .emailAddress ? .never : .words)
                    .autocorrectionDisabled(keyboard == .emailAddress)
            }
            .padding(14).background(.white, in: RoundedRectangle(cornerRadius: 13))
            .overlay(RoundedRectangle(cornerRadius: 13).stroke(BelozStyle.line, lineWidth: 1))
        }
    }

    @MainActor private func submit() async {
        error = nil; notice = nil
        guard email.contains("@"), !password.isEmpty else { error = "Introduce un correo válido y una contraseña."; return }
        busy = true
        defer { busy = false }
        do {
            if isRegister {
                guard !name.isEmpty, !surname.isEmpty, !phone.isEmpty else { error = "Completa nombre, apellidos y teléfono."; return }
                try await store.register(name: name, surname: surname, email: email, password: password, phone: phone)
                isRegister = false
                password = ""
                notice = "Registro completado. Ya puedes iniciar sesión."
            } else {
                try await store.login(email: email, password: password)
                dismiss()
            }
        } catch { self.error = error.localizedDescription }
    }
}

struct AccountSettingsSheet: View {
    @EnvironmentObject private var store: BelozStore
    @Environment(\.dismiss) private var dismiss
    @State private var email = ""
    @State private var phone = ""
    @State private var status: String?
    @State private var error: String?
    @State private var busy = false

    var body: some View {
        NavigationStack {
            Form {
                Section("Correo electrónico") {
                    TextField("Correo", text: $email).keyboardType(.emailAddress).textInputAutocapitalization(.never).autocorrectionDisabled()
                    Button("Actualizar correo") { Task { await updateEmail() } }.disabled(busy)
                }
                Section("Teléfono") {
                    TextField("Teléfono", text: $phone).keyboardType(.phonePad)
                    Button("Actualizar teléfono") { Task { await updatePhone() } }.disabled(busy)
                }
                if busy { ProgressView().frame(maxWidth: .infinity) }
                if let status { Section { Text(status).foregroundStyle(BelozStyle.green) } }
                if let error { Section { Text(error).foregroundStyle(BelozStyle.danger) } }
            }
            .scrollContentBackground(.hidden).background(BelozStyle.canvas)
            .navigationTitle("Datos personales").navigationBarTitleDisplayMode(.inline)
            .toolbar { ToolbarItem(placement: .topBarTrailing) { Button("Cerrar") { dismiss() } } }
            .onAppear { email = store.session?.email ?? ""; phone = store.session?.phone ?? "" }
        }
    }

    @MainActor private func updateEmail() async {
        busy = true; error = nil; status = nil
        defer { busy = false }
        do { try await store.updateEmail(email); status = "Correo actualizado." }
        catch { self.error = error.localizedDescription }
    }

    @MainActor private func updatePhone() async {
        busy = true; error = nil; status = nil
        defer { busy = false }
        do { try await store.updatePhone(phone); status = "Teléfono actualizado." }
        catch { self.error = error.localizedDescription }
    }
}

struct PasswordChangeSheet: View {
    @EnvironmentObject private var store: BelozStore
    @Environment(\.dismiss) private var dismiss
    @State private var current = ""
    @State private var newPassword = ""
    @State private var confirmation = ""
    @State private var error: String?
    @State private var saved = false
    @State private var busy = false

    var body: some View {
        NavigationStack {
            Form {
                Section("Cambiar contraseña") {
                    SecureField("Contraseña actual", text: $current)
                    SecureField("Nueva contraseña", text: $newPassword)
                    SecureField("Repetir nueva contraseña", text: $confirmation)
                }
                if let error { Text(error).foregroundStyle(BelozStyle.danger) }
                if saved { Text("Contraseña actualizada.").foregroundStyle(BelozStyle.green) }
                Button {
                    Task { await change() }
                } label: {
                    HStack { Spacer(); if busy { ProgressView() }; Text("Actualizar contraseña"); Spacer() }
                }.disabled(busy)
            }
            .scrollContentBackground(.hidden).background(BelozStyle.canvas)
            .navigationTitle("Seguridad").navigationBarTitleDisplayMode(.inline)
            .toolbar { ToolbarItem(placement: .topBarTrailing) { Button("Cerrar") { dismiss() } } }
        }
    }

    @MainActor private func change() async {
        error = nil; saved = false
        guard !current.isEmpty, newPassword.count >= 8, newPassword == confirmation else {
            error = "Comprueba la contraseña actual y que la nueva tenga al menos 8 caracteres y coincida."; return
        }
        busy = true; defer { busy = false }
        do { try await store.updatePassword(current: current, new: newPassword); saved = true; current = ""; newPassword = ""; confirmation = "" }
        catch { self.error = error.localizedDescription }
    }
}
