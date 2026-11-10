import SwiftUI

enum BelozStyle {
    static let canvas = Color(hex: 0xF8FAF9)
    static let surface = Color.white
    static let ink = Color(hex: 0x17251F)
    static let forest = Color(hex: 0x12372D)
    static let green = Color(hex: 0x176447)
    static let accent = Color(hex: 0x56E39F)
    static let mint = Color(hex: 0xE5F4EC)
    static let lime = Color(hex: 0x56E39F)
    static let gold = Color(hex: 0xF7D560)
    static let orange = Color(hex: 0xFFB24D)
    static let muted = Color(hex: 0x52645A)
    static let line = Color(hex: 0xDCE4DF)
    static let danger = Color(hex: 0xB83A3A)
}

extension Color {
    init(hex: UInt) {
        self.init(.sRGB, red: Double((hex >> 16) & 0xff) / 255,
                  green: Double((hex >> 8) & 0xff) / 255,
                  blue: Double(hex & 0xff) / 255, opacity: 1)
    }
}

struct ScreenBackdrop<Content: View>: View {
    @ViewBuilder var content: Content
    var body: some View {
        ZStack {
            BelozStyle.canvas.ignoresSafeArea()
            content
        }
        .foregroundStyle(BelozStyle.ink)
    }
}

struct Eyebrow: View {
    let text: String
    var body: some View {
        Text(text.uppercased())
            .font(.system(size: 11, weight: .bold, design: .rounded))
            .tracking(1.1)
            .foregroundStyle(BelozStyle.green)
    }
}

struct SectionTitle: View {
    let title: String
    var subtitle: String? = nil
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(title).font(.system(size: 22, weight: .bold, design: .rounded)).foregroundStyle(BelozStyle.ink)
            if let subtitle { Text(subtitle).font(.subheadline).foregroundStyle(BelozStyle.muted) }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct InlineNotice: View {
    let text: String
    var isError = false
    var body: some View {
        HStack(alignment: .top, spacing: 10) {
            Image(systemName: isError ? "exclamationmark.circle.fill" : "info.circle.fill")
                .foregroundStyle(isError ? BelozStyle.danger : BelozStyle.green)
            Text(text).font(.footnote).foregroundStyle(BelozStyle.ink).fixedSize(horizontal: false, vertical: true)
        }
        .padding(12)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(isError ? Color(hex: 0xFCEDEC) : BelozStyle.mint, in: RoundedRectangle(cornerRadius: 14))
    }
}

struct PrimaryButtonStyle: ButtonStyle {
    var fill: Color = BelozStyle.accent
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 15, weight: .semibold, design: .rounded))
            .foregroundStyle(BelozStyle.ink)
            .padding(.horizontal, 18)
            .frame(minHeight: 48)
            .background(fill.opacity(configuration.isPressed ? 0.82 : 1), in: RoundedRectangle(cornerRadius: 18))
            .scaleEffect(configuration.isPressed ? 0.98 : 1)
    }
}

struct FilterChip: View {
    let title: String
    var selected: Bool
    var action: () -> Void
    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 13, weight: .semibold))
                .foregroundStyle(selected ? BelozStyle.ink : BelozStyle.muted)
                .padding(.horizontal, 14)
                .frame(minHeight: 38)
                .background(selected ? Color(hex: 0xE8DEF8) : .clear, in: RoundedRectangle(cornerRadius: 8))
                .overlay(RoundedRectangle(cornerRadius: 8).stroke(selected ? .clear : BelozStyle.muted.opacity(0.7), lineWidth: 1))
        }
        .buttonStyle(.plain)
    }
}

struct RemoteFoodImage: View {
    let path: String?
    var height: CGFloat
    var body: some View {
        Group {
            if let url = BelozAPI.imageURL(path) {
                AsyncImage(url: url) { phase in
                    switch phase {
                    case .success(let image): image.resizable().scaledToFill()
                    case .empty:
                        ZStack { BelozStyle.canvas; ProgressView().tint(BelozStyle.green) }
                    case .failure: placeholder
                    @unknown default: placeholder
                    }
                }
            } else {
                placeholder
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: height)
        .clipped()
        .accessibilityLabel("Imagen de comida")
    }

    private var placeholder: some View {
        ZStack {
            BelozStyle.canvas
            Text("Sin foto").font(.caption).foregroundStyle(BelozStyle.muted)
        }
        .frame(maxWidth: .infinity)
        .frame(height: height)
    }
}

struct RestaurantRow: View {
    let restaurant: Restaurant
    var compact = false
    var body: some View {
        HStack(spacing: 14) {
            RemoteFoodImage(path: restaurant.imagePath, height: compact ? 60 : 88)
                .frame(width: compact ? 60 : 88)
                .clipShape(RoundedRectangle(cornerRadius: 6))
            VStack(alignment: .leading, spacing: 6) {
                Text(restaurant.name).font(.system(size: 16, weight: .semibold)).lineLimit(3)
                Text([restaurant.foodType, restaurant.country].filter { !$0.isEmpty }.joined(separator: " · "))
                    .font(.caption).foregroundStyle(BelozStyle.muted).lineLimit(2)
                HStack(spacing: 12) {
                    if restaurant.rating > 0 {
                        Label(String(format: "%.1f", restaurant.rating), systemImage: "star.fill")
                            .foregroundStyle(BelozStyle.ink)
                    }
                    Text(BelozDiscovery.priceLabel(restaurant.priceLevel)).foregroundStyle(BelozStyle.muted)
                    Text(restaurant.waitTime > 0 ? "~\(restaurant.waitTime) min" : "Espera sin indicar")
                        .foregroundStyle(BelozStyle.muted)
                }
                .font(.caption.weight(.medium))
            }
            Spacer(minLength: 0)
            Image(systemName: "chevron.right").font(.caption.weight(.bold)).foregroundStyle(BelozStyle.green)
        }
        .padding(12)
        .background(.white, in: RoundedRectangle(cornerRadius: 8))
        .overlay(RoundedRectangle(cornerRadius: 8).stroke(BelozStyle.line, lineWidth: 1))
        .contentShape(RoundedRectangle(cornerRadius: 8))
    }
}

struct DishRow: View {
    @EnvironmentObject private var store: BelozStore
    let dish: Dish
    @State private var showReplaceCart = false
    @State private var showDetails = false

    private var validPrice: Bool { dish.price.isFinite && dish.price > 0 }
    private var quantity: Int { store.cart.first(where: { $0.dish.id == dish.id })?.quantity ?? 0 }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Button { showDetails = true } label: {
                HStack(alignment: .top, spacing: 12) {
                    VStack(alignment: .leading, spacing: 5) {
                        Text(dish.name).font(.system(size: 16, weight: .semibold)).foregroundStyle(BelozStyle.ink)
                        if !dish.details.isEmpty {
                            Text(dish.details).font(.caption).foregroundStyle(BelozStyle.muted).lineLimit(3)
                        }
                    }.frame(maxWidth: .infinity, alignment: .leading)
                    RemoteFoodImage(path: dish.imagePath, height: 84).frame(width: 84)
                        .clipShape(RoundedRectangle(cornerRadius: 6))
                }.contentShape(Rectangle())
            }
            .buttonStyle(.plain).accessibilityLabel("Ver detalle de \(dish.name)")
            HStack(spacing: 8) {
                VStack(alignment: .leading, spacing: 3) {
                    Text(validPrice ? dish.price.euro : "Precio no disponible")
                        .font(.subheadline.weight(.semibold)).foregroundStyle(BelozStyle.ink)
                    if quantity > 0 { Text("\(quantity) en carrito").font(.caption).foregroundStyle(BelozStyle.green) }
                }
                Spacer()
                Button {
                    if !store.addToCart(dish) { showReplaceCart = true }
                } label: {
                    Label("Añadir", systemImage: "plus")
                        .font(.subheadline.weight(.semibold)).foregroundStyle(BelozStyle.ink)
                        .padding(.horizontal, 12).frame(minHeight: 48)
                        .background(BelozStyle.mint, in: RoundedRectangle(cornerRadius: 8))
                }
                .disabled(!validPrice || quantity >= 99)
                .accessibilityLabel("Añadir \(dish.name) al carrito")
            }
        }
        .padding(12).frame(maxWidth: .infinity, alignment: .leading)
        .background(.white, in: RoundedRectangle(cornerRadius: 8))
        .overlay(RoundedRectangle(cornerRadius: 8).stroke(BelozStyle.line, lineWidth: 1))
        .sheet(isPresented: $showDetails) {
            NavigationStack {
                ScrollView {
                    VStack(alignment: .leading, spacing: 12) {
                        RemoteFoodImage(path: dish.imagePath, height: 170)
                            .clipShape(RoundedRectangle(cornerRadius: 8))
                        Text(dish.details.isEmpty ? "Descripción no disponible." : dish.details)
                        Text("Información del catálogo. Confirma ingredientes y disponibilidad con el restaurante.")
                            .font(.footnote).foregroundStyle(BelozStyle.muted)
                    }.padding(18)
                }
                .background(BelozStyle.canvas)
                .navigationTitle(dish.name).navigationBarTitleDisplayMode(.inline)
                .toolbar { ToolbarItem(placement: .topBarTrailing) { Button("Cerrar") { showDetails = false } } }
            }
        }
        .alert("Tu carrito tiene platos de otro restaurante", isPresented: $showReplaceCart) {
            Button("Vaciar y añadir", role: .destructive) {
                _ = store.addToCart(dish, replacingExisting: true)
            }
            Button("Cancelar", role: .cancel) { }
        } message: { Text("Solo puedes preparar un pedido de un restaurante cada vez.") }
    }
}
