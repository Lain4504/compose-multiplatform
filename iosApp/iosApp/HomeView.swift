import SwiftUI
import Shared

struct HomeView: View {
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Welcome Card
                VStack(spacing: 16) {
                    Text("Welcome to Compose Multiplatform!")
                        .font(.title2)
                        .fontWeight(.bold)
                        .multilineTextAlignment(.center)
                    
                    Text("SwiftUI: \(Greeting().greet())")
                        .font(.body)
                        .foregroundColor(.secondary)
                }
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(12)
                
                // Features List
                VStack(alignment: .leading, spacing: 12) {
                    Text("Features")
                        .font(.title3)
                        .fontWeight(.semibold)
                    
                    VStack(alignment: .leading, spacing: 8) {
                        FeatureRow(icon: "🔢", text: "Counter - Increment/Decrement demo")
                        FeatureRow(icon: "🧮", text: "Calculator - Basic calculations")
                        FeatureRow(icon: "✅", text: "Todo List - Task management")
                        FeatureRow(icon: "⏱️", text: "Timer/Stopwatch - Time tracking")
                        FeatureRow(icon: "📝", text: "Notes - Colorful note taking")
                        FeatureRow(icon: "💻", text: "Shared business logic across platforms")
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding()
                .background(Color(.systemBackground))
                .cornerRadius(12)
                .shadow(color: .black.opacity(0.1), radius: 5, x: 0, y: 2)
            }
            .padding()
        }
        .navigationTitle("Home")
    }
}

struct FeatureRow: View {
    let icon: String
    let text: String
    
    var body: some View {
        HStack(spacing: 12) {
            Text(icon)
                .font(.title3)
            Text(text)
                .font(.body)
        }
    }
}

#Preview {
    NavigationStack {
        HomeView()
    }
}

