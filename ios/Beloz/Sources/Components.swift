import SwiftUI

enum BelozStyle {
    static let canvas = Color(hex: 0xF5F7F4)
    static let surface = Color.white
    static let ink = Color(hex: 0x142820)
    static let forest = Color(hex: 0x123C31)
    static let green = Color(hex: 0x168B68)
    static let mint = Color(hex: 0xD8F2E7)
    static let lime = Color(hex: 0xC6E8A0)
    static let gold = Color(hex: 0xF3B54A)
    static let muted = Color(hex: 0x718078)
    static let line = Color(hex: 0xE4EAE5)
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
    var fill: Color = BelozStyle.forest
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .font(.system(size: 15, weight: .semibold, design: .rounded))
            .foregroundStyle(.white)
            .padding(.horizontal, 18)
            .frame(minHeight: 48)
            .background(fill.opacity(configuration.isPressed ? 0.82 : 1), in: Capsule())
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
                .foregroundStyle(selected ? BelozStyle.forest : BelozStyle.muted)
                .padding(.horizontal, 14)
                .frame(minHeight: 38)
                .background(selected ? BelozStyle.mint : .white, in: Capsule())
                .overlay(Capsule().stroke(selected ? BelozStyle.green.opacity(0.35) : BelozStyle.line, lineWidth: 1))
        }
        .buttonStyle(.plain)
    }
}

struct RemoteFoodImage: View {
    let path: String?
    var height: CGFloat
    var body: some View {
        AsyncImage(url: BelozAPI.imageURL(path)) { phase in
            switch phase {
            case .success(let image): image.resizable().scaledToFill()
            case .empty, .failure: placeholder
            @unknown default: placeholder
            }
        }
        .frame(maxWidth: .infinity)
        .frame(height: height)
        .clipped()
        .accessibilityLabel("Imagen de comida")
    }

    private var placeholder: some View {
        ZStack {
            Color(hex: 0xE7F1E9)
            Image(systemName: "fork.knife.circle.fill")
                .font(.system(size: 34, weight: .light))
                .foregroundStyle(BelozStyle.green.opacity(0.8))
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
            RemoteFoodImage(path: restaurant.imagePath, height: compact ? 80 : 94)
                .frame(width: compact ? 88 : 104)
                .clipShape(RoundedRectangle(cornerRadius: 15))
            VStack(alignment: .leading, spacing: 6) {
                Text(restaurant.name).font(.system(size: 17, weight: .bold, design: .rounded)).lineLimit(1)
                Text(restaurant.foodType.isEmpty ? "Restaurante" : restaurant.foodType)
                    .font(.subheadline).foregroundStyle(BelozStyle.muted).lineLimit(1)
                HStack(spacing: 12) {
                    if restaurant.rating > 0 {
                        Label(String(format: "%.1f", restaurant.rating), systemImage: "star.fill")
                            .foregroundStyle(BelozStyle.ink)
                    }
                    if restaurant.waitTime > 0 {
                        Label("\(restaurant.waitTime) min", systemImage: "clock")
                            .foregroundStyle(BelozStyle.muted)
                    }
                    if !restaurant.priceLevel.isEmpty {
                        Text(restaurant.priceLevel).foregroundStyle(BelozStyle.muted)
                    }
                }
                .font(.caption.weight(.medium))
            }
            Spacer(minLength: 0)
            Image(systemName: "chevron.right").font(.caption.weight(.bold)).foregroundStyle(BelozStyle.green)
        }
        .padding(12)
        .background(.white, in: RoundedRectangle(cornerRadius: 18))
        .overlay(RoundedRectangle(cornerRadius: 18).stroke(BelozStyle.line, lineWidth: 1))
        .contentShape(RoundedRectangle(cornerRadius: 18))
    }
}

struct DishRow: View {
    @EnvironmentObject private var store: BelozStore
    let dish: Dish
    @State private var showReplaceCart = false
    @State private var added = false

    var body: some View {
        HStack(spacing: 13) {
            if dish.imagePath != nil {
                RemoteFoodImage(path: dish.imagePath, height: 76)
                    .frame(width: 82)
                    .clipShape(RoundedRectangle(cornerRadius: 14))
            }
            VStack(alignment: .leading, spacing: 5) {
                Text(dish.name).font(.system(size: 16, weight: .semibold, design: .rounded))
                if !dish.details.isEmpty {
                    Text(dish.details).font(.subheadline).foregroundStyle(BelozStyle.muted).lineLimit(2)
                }
                Text(dish.price.euro).font(.subheadline.weight(.bold)).foregroundStyle(BelozStyle.forest).padding(.top, 2)
            }
            Spacer(minLength: 4)
            Button {
                if !store.addToCart(dish) { showReplaceCart = true }
                else { added = true; DispatchQueue.main.asyncAfter(deadline: .now() + 1.1) { added = false } }
            } label: {
                Image(systemName: added ? "checkmark" : "plus")
                    .font(.system(size: 15, weight: .bold))
                    .foregroundStyle(.white)
                    .frame(width: 38, height: 38)
                    .background(added ? BelozStyle.green : BelozStyle.forest, in: Circle())
            }
            .buttonStyle(.plain)
            .accessibilityLabel("Añadir \(dish.name) al carrito")
        }
        .padding(.vertical, 10)
        .alert("Tu carrito tiene platos de otro restaurante", isPresented: $showReplaceCart) {
            Button("Vaciar y añadir", role: .destructive) {
                _ = store.addToCart(dish, replacingExisting: true)
                added = true
            }
            Button("Cancelar", role: .cancel) { }
        } message: { Text("Solo puedes preparar un pedido de un restaurante cada vez.") }
    }
}
