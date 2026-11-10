import SwiftUI

struct CuisinePickerView: View {
    @Binding var selection: Set<String>
    var onShowResults: (Set<String>) -> Void

    private let columns = [GridItem(.adaptive(minimum: 96), spacing: 8)]

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            HStack(alignment: .bottom) {
                VStack(alignment: .leading, spacing: 3) {
                    Text("Tipo de comida").font(.system(size: 20, weight: .black))
                    Text("Elige uno o varios estilos").font(.caption).foregroundStyle(BelozStyle.muted)
                }
                Spacer()
                if !selection.isEmpty {
                    Text("\(selection.count) activos").font(.caption.weight(.bold)).foregroundStyle(BelozStyle.green)
                }
            }
            ScrollView {
                LazyVGrid(columns: columns, spacing: 8) {
                    ForEach(BelozDiscovery.cuisines, id: \.self) { cuisine in
                        CuisineTile(label: cuisine, selected: selection.contains(cuisine)) {
                            if selection.contains(cuisine) { selection.remove(cuisine) }
                            else { selection.insert(cuisine) }
                        }
                    }
                }
                .padding(2)
            }
            Button("Ver resultados") { onShowResults(selection) }
                .font(.system(size: 15, weight: .bold))
                .foregroundStyle(BelozStyle.ink)
                .frame(maxWidth: .infinity, minHeight: 48)
                .background(selection.isEmpty ? BelozStyle.line : BelozStyle.accent, in: RoundedRectangle(cornerRadius: 18))
                .disabled(selection.isEmpty)
        }
        .padding(16)
        .background(BelozStyle.canvas)
        .navigationBarTitleDisplayMode(.inline)
    }
}

private struct CuisineTile: View {
    @Environment(\.accessibilityReduceMotion) private var reduceMotion
    let label: String
    let selected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            VStack(spacing: 6) {
                ZStack {
                    if selected {
                        PaintMark().fill(BelozStyle.orange)
                            .rotationEffect(.degrees(Double((label.count % 7) - 3) * 4))
                            .scaleEffect(x: 1.05, y: 0.96)
                    }
                    Image(assetName)
                        .resizable().scaledToFit().frame(width: 48, height: 48)
                        .accessibilityHidden(true)
                }
                .frame(width: 66, height: 66)
                .scaleEffect(selected && !reduceMotion ? 1.06 : 1)
                .animation(reduceMotion ? nil : .spring(response: 0.32, dampingFraction: 0.55), value: selected)
                Text(label)
                    .font(.system(size: 13, weight: selected ? .bold : .medium))
                    .foregroundStyle(BelozStyle.ink).multilineTextAlignment(.center)
                    .lineLimit(2).frame(maxWidth: .infinity, minHeight: 32)
            }
            .padding(.horizontal, 6).padding(.vertical, 8)
            .frame(maxWidth: .infinity, minHeight: 124)
            .background(selected ? BelozStyle.mint : .white, in: RoundedRectangle(cornerRadius: 8))
            .overlay(RoundedRectangle(cornerRadius: 8).stroke(selected ? BelozStyle.orange.opacity(0.35) : .clear, lineWidth: 1))
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
        .accessibilityLabel(label)
        .accessibilityAddTraits(selected ? .isSelected : [])
    }

    private var assetName: String {
        switch label {
        case "Pizza": return "FoodPizza"
        case "Kebab": return "FoodKebab"
        case "Ramen": return "FoodRamen"
        case "Nachos": return "FoodNachos"
        case "Americana": return "FoodAmericana"
        case "Arabe": return "FoodArabe"
        case "Sushi": return "FoodSushi"
        case "India": return "FoodIndia"
        case "Poke": return "FoodPoke"
        case "Peruana": return "FoodPeruana"
        case "Hamburguesas": return "FoodHamburguesas"
        default: return "FoodPizza"
        }
    }
}

private struct PaintMark: Shape {
    func path(in rect: CGRect) -> Path {
        let w = rect.width, h = rect.height
        var path = Path()
        path.move(to: CGPoint(x: w * 0.12, y: h * 0.52))
        path.addCurve(to: CGPoint(x: w * 0.31, y: h * 0.08), control1: CGPoint(x: w * 0.02, y: h * 0.30), control2: CGPoint(x: w * 0.26, y: h * 0.18))
        path.addCurve(to: CGPoint(x: w * 0.70, y: h * 0.12), control1: CGPoint(x: w * 0.42, y: h * -0.08), control2: CGPoint(x: w * 0.55, y: h * 0.18))
        path.addCurve(to: CGPoint(x: w * 0.98, y: h * 0.48), control1: CGPoint(x: w * 0.94, y: h * 0.02), control2: CGPoint(x: w * 0.90, y: h * 0.36))
        path.addCurve(to: CGPoint(x: w * 0.74, y: h * 0.84), control1: CGPoint(x: w * 1.10, y: h * 0.68), control2: CGPoint(x: w * 0.76, y: h * 0.66))
        path.addCurve(to: CGPoint(x: w * 0.31, y: h * 0.96), control1: CGPoint(x: w * 0.71, y: h * 1.06), control2: CGPoint(x: w * 0.45, y: h * 0.82))
        path.addCurve(to: CGPoint(x: w * 0.12, y: h * 0.52), control1: CGPoint(x: w * 0.14, y: h * 1.10), control2: CGPoint(x: w * 0.23, y: h * 0.70))
        path.closeSubpath()
        path.move(to: CGPoint(x: w * 0.08, y: h * 0.74))
        path.addCurve(to: CGPoint(x: w * 0.23, y: h * 0.88), control1: CGPoint(x: w * 0.00, y: h * 0.82), control2: CGPoint(x: w * 0.10, y: h * 0.95))
        path.addCurve(to: CGPoint(x: w * 0.08, y: h * 0.74), control1: CGPoint(x: w * 0.18, y: h * 0.80), control2: CGPoint(x: w * 0.16, y: h * 0.75))
        path.closeSubpath()
        path.move(to: CGPoint(x: w * 0.70, y: h * 0.07))
        path.addCurve(to: CGPoint(x: w * 0.82, y: h * 0.17), control1: CGPoint(x: w * 0.76, y: h * -0.03), control2: CGPoint(x: w * 0.89, y: h * 0.05))
        path.addCurve(to: CGPoint(x: w * 0.70, y: h * 0.07), control1: CGPoint(x: w * 0.76, y: h * 0.15), control2: CGPoint(x: w * 0.73, y: h * 0.12))
        path.closeSubpath()
        return path
    }
}
