import SwiftUI

struct OrdersView: View {
    @EnvironmentObject private var store: BelozStore
    @State private var showAuth = false

    var body: some View {
        ScreenBackdrop {
            Group {
                if store.session == nil {
                    VStack(spacing: 16) {
                        ContentUnavailableView("Inicia sesión", systemImage: "person.crop.circle",
                            description: Text("Accede a tu cuenta para ver tus pedidos."))
                        Button("Iniciar sesión") { showAuth = true }.buttonStyle(PrimaryButtonStyle())
                    }
                } else if store.isLoadingOrders && store.orders.isEmpty {
                    ProgressView("Cargando pedidos…")
                } else if let error = store.ordersError {
                    VStack(spacing: 14) {
                        InlineNotice(text: error, isError: true)
                        Button("Reintentar") { Task { await store.loadOrders() } }.buttonStyle(PrimaryButtonStyle())
                    }.padding(18)
                } else if store.orders.isEmpty {
                    ContentUnavailableView("No tienes pedidos", systemImage: "bag",
                        description: Text("Tus pedidos aparecerán aquí."))
                } else {
                    List {
                        Section("Pedido más reciente") { orderLink(store.orders[0]) }
                        if store.orders.count > 1 {
                            Section("Pedidos anteriores") {
                                ForEach(Array(store.orders.dropFirst())) { order in orderLink(order) }
                            }
                        }
                    }
                    .scrollContentBackground(.hidden)
                    .refreshable { await store.loadOrders() }
                }
            }
        }
        .navigationTitle("Mis pedidos")
        .navigationBarTitleDisplayMode(.inline)
        .task(id: store.session?.id) { if store.session != nil { await store.loadOrders() } }
        .sheet(isPresented: $showAuth) { AccountAuthSheet() }
    }

    private func orderLink(_ order: Order) -> some View {
        NavigationLink(destination: OrderDetailView(order: order)) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Pedido #\(order.id)").font(.headline)
                    if !order.date.isEmpty { Text(order.date).font(.caption).foregroundStyle(BelozStyle.muted) }
                }
                Spacer()
                Text(order.total.euro).font(.subheadline.weight(.semibold))
            }.padding(.vertical, 6)
        }
    }
}

struct OrderDetailView: View {
    @EnvironmentObject private var store: BelozStore
    let order: Order
    @State private var details: [OrderDetail] = []
    @State private var error: String?
    @State private var isLoading = true

    var body: some View {
        ScreenBackdrop {
            Group {
                if isLoading {
                    ProgressView("Cargando pedido…")
                } else if let error {
                    VStack(spacing: 14) {
                        InlineNotice(text: error, isError: true)
                        Button("Reintentar") { Task { await load() } }.buttonStyle(PrimaryButtonStyle())
                    }.padding(18)
                } else if details.isEmpty {
                    ContentUnavailableView("Sin detalles", systemImage: "receipt",
                        description: Text("El servicio no ha devuelto platos para este pedido."))
                } else {
                    List {
                        if let name = details.first?.restaurantName, !name.isEmpty {
                            Section { Text(name).font(.headline) }
                        }
                        Section("Platos") {
                            ForEach(details) { detail in
                                HStack {
                                    VStack(alignment: .leading, spacing: 4) {
                                        Text(detail.name).font(.subheadline.weight(.semibold))
                                        Text("Cantidad: \(detail.quantity)").font(.caption).foregroundStyle(BelozStyle.muted)
                                    }
                                    Spacer()
                                    Text((detail.price * Double(detail.quantity)).euro)
                                        .font(.subheadline.weight(.semibold))
                                }
                            }
                        }
                        Section { HStack { Text("Total"); Spacer(); Text(order.total.euro).bold() } }
                    }.scrollContentBackground(.hidden)
                }
            }
        }
        .navigationTitle("Pedido #\(order.id)")
        .navigationBarTitleDisplayMode(.inline)
        .task { await load() }
    }

    private func load() async {
        isLoading = true
        error = nil
        defer { isLoading = false }
        do { details = try await store.loadOrderDetails(for: order) }
        catch { self.error = error.localizedDescription }
    }
}
