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
                            if store.isSubmittingOrder { ProgressView().tint(.white) }
                            Text(store.session == nil ? "Iniciar sesión para pedir" : "Enviar pedido")
                            Spacer()
                            Text(store.cartTotal.euro)
                        }
                        .padding(.horizontal, 18).frame(maxWidth: .infinity, minHeight: 52)
                    }
                    .buttonStyle(PrimaryButtonStyle())
                    .disabled(store.isSubmittingOrder)
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
                Text(line.lineTotal.euro).font(.caption.weight(.semibold)).foregroundStyle(BelozStyle.green)
            }
            Spacer()
            HStack(spacing: 12) {
                Button { setQuantity(line.quantity - 1) } label: { Image(systemName: "minus.circle").font(.title3) }
                Text("\(line.quantity)").font(.subheadline.monospacedDigit().weight(.semibold)).frame(minWidth: 16)
                Button { setQuantity(line.quantity + 1) } label: { Image(systemName: "plus.circle").font(.title3) }
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
    @State private var showSettings = false
    @State private var showPassword = false
    @State private var confirmDelete = false
    @State private var alertText: String?

    var body: some View {
        ScreenBackdrop {
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 18) {
                    SectionTitle(title: "Cuenta", subtitle: "Tus datos y pedidos")
                    if let user = store.session {
                        VStack(alignment: .leading, spacing: 14) {
                            HStack(spacing: 13) {
                                Text(String(user.name.prefix(1)).uppercased().nonEmpty ?? "B")
                                    .font(.system(size: 23, weight: .bold, design: .rounded)).foregroundStyle(BelozStyle.forest)
                                    .frame(width: 54, height: 54).background(BelozStyle.mint, in: Circle())
                                VStack(alignment: .leading, spacing: 4) {
                                    Eyebrow(text: "Bienvenido/a")
                                    Text(user.displayName.isEmpty ? user.email : user.displayName)
                                        .font(.system(size: 21, weight: .bold, design: .rounded)).lineLimit(1)
                                    Text(user.email).font(.subheadline).foregroundStyle(BelozStyle.muted)
                                }
                            }
                            if !user.phone.isEmpty { Label(user.phone, systemImage: "phone").font(.subheadline).foregroundStyle(BelozStyle.muted) }
                            Divider().overlay(BelozStyle.line)
                            HStack(spacing: 10) {
                                Button("Editar datos") { showSettings = true }.buttonStyle(PrimaryButtonStyle(fill: BelozStyle.green))
                                Button("Seguridad") { showPassword = true }
                                    .font(.subheadline.weight(.semibold)).foregroundStyle(BelozStyle.forest)
                                    .frame(minHeight: 46).padding(.horizontal, 15)
                                    .background(BelozStyle.mint, in: Capsule())
                            }
                        }
                        .padding(17).background(.white, in: RoundedRectangle(cornerRadius: 20))
                        .overlay(RoundedRectangle(cornerRadius: 20).stroke(BelozStyle.line, lineWidth: 1))

                        HStack {
                            SectionTitle(title: "Mis pedidos")
                            Spacer()
                            if store.isLoadingOrders { ProgressView().controlSize(.small) }
                        }
                        if store.orders.isEmpty && !store.isLoadingOrders {
                            Text("Todavía no tienes pedidos registrados.")
                                .font(.subheadline).foregroundStyle(BelozStyle.muted)
                                .frame(maxWidth: .infinity, alignment: .leading).padding(16)
                                .background(.white, in: RoundedRectangle(cornerRadius: 16))
                        } else {
                            VStack(spacing: 0) {
                                ForEach(store.orders) { order in
                                    HStack {
                                        VStack(alignment: .leading, spacing: 4) {
                                            Text("Pedido #\(order.id)").font(.subheadline.weight(.semibold))
                                            Text(order.date).font(.caption).foregroundStyle(BelozStyle.muted).lineLimit(1)
                                        }
                                        Spacer()
                                        Text(order.total.euro).font(.subheadline.weight(.bold)).foregroundStyle(BelozStyle.forest)
                                    }.padding(14)
                                    if order.id != store.orders.last?.id { Divider().overlay(BelozStyle.line) }
                                }
                            }
                            .background(.white, in: RoundedRectangle(cornerRadius: 17))
                        }

                        Button("Cerrar sesión") { Task { await store.logout() } }
                            .font(.subheadline.weight(.semibold)).foregroundStyle(BelozStyle.muted)
                            .frame(maxWidth: .infinity, minHeight: 46)
                        Button("Eliminar mi cuenta", role: .destructive) { confirmDelete = true }
                            .font(.caption.weight(.semibold)).frame(maxWidth: .infinity)
                    } else {
                        VStack(alignment: .leading, spacing: 14) {
                            Image(systemName: "person.crop.circle.badge.checkmark").font(.system(size: 38)).foregroundStyle(BelozStyle.green)
                            Text("Tu cuenta Beloz").font(.system(size: 22, weight: .bold, design: .rounded))
                            Text("Inicia sesión para consultar tus pedidos y mantener tu perfil.")
                                .font(.subheadline).foregroundStyle(BelozStyle.muted)
                            Button("Iniciar sesión o registrarme") { showAuth = true }.buttonStyle(PrimaryButtonStyle())
                        }
                        .padding(19).frame(maxWidth: .infinity, alignment: .leading)
                        .background(.white, in: RoundedRectangle(cornerRadius: 20))
                        .overlay(RoundedRectangle(cornerRadius: 20).stroke(BelozStyle.line, lineWidth: 1))
                    }
                    VStack(alignment: .leading, spacing: 6) {
                        Eyebrow(text: "Privacidad")
                        Text("La sesión se guarda en el llavero seguro de este dispositivo. Esta app no almacena tarjetas ni procesa pagos online.")
                            .font(.caption).foregroundStyle(BelozStyle.muted).fixedSize(horizontal: false, vertical: true)
                    }.padding(.top, 8)
                }
                .padding(18).padding(.bottom, 25)
            }
        }
        .navigationTitle("Cuenta")
        .navigationBarTitleDisplayMode(.inline)
        .task { if store.session != nil { await store.loadOrders() } }
        .sheet(isPresented: $showAuth) { AccountAuthSheet() }
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
}

struct AccountAuthSheet: View {
    @EnvironmentObject private var store: BelozStore
    @Environment(\.dismiss) private var dismiss
    @State private var isRegister = false
    @State private var name = ""
    @State private var surname = ""
    @State private var phone = ""
    @State private var email = ""
    @State private var password = ""
    @State private var busy = false
    @State private var error: String?
    @State private var notice: String?

    var body: some View {
        NavigationStack {
            ScreenBackdrop {
                ScrollView {
                    VStack(alignment: .leading, spacing: 14) {
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
                                if busy { ProgressView().tint(.white) }
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
        catch { error = error.localizedDescription }
    }
}
