import SwiftUI
import Shared

struct CounterView: View {
    @StateObject private var viewModel = CounterViewModel()
    
    var body: some View {
        VStack(spacing: 32) {
            VStack(spacing: 16) {
                Text("Counter Demo")
                    .font(.title2)
                    .fontWeight(.bold)
                
                Text("\(viewModel.count)")
                    .font(.system(size: 72, weight: .bold))
                    .foregroundColor(.blue)
                    .padding()
                    .frame(minWidth: 150)
                    .background(Color(.systemGray6))
                    .cornerRadius(16)
                
                HStack(spacing: 16) {
                    Button(action: {
                        viewModel.decrement()
                    }) {
                        Label("Decrement", systemImage: "minus.circle.fill")
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.red)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                    }
                    
                    Button(action: {
                        viewModel.reset()
                    }) {
                        Label("Reset", systemImage: "arrow.counterclockwise")
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.orange)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                    }
                    
                    Button(action: {
                        viewModel.increment()
                    }) {
                        Label("Increment", systemImage: "plus.circle.fill")
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.green)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                    }
                }
            }
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(16)
            .shadow(color: .black.opacity(0.1), radius: 10, x: 0, y: 5)
            
            Text("This counter uses shared Kotlin business logic")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding()
        .navigationTitle("Counter")
    }
}

class CounterViewModel: ObservableObject {
    private let counter = Counter()
    @Published var count: Int32 = 0
    
    init() {
        count = counter.getValue()
    }
    
    func increment() {
        count = counter.increment()
    }
    
    func decrement() {
        count = counter.decrement()
    }
    
    func reset() {
        count = counter.reset()
    }
}

#Preview {
    NavigationStack {
        CounterView()
    }
}

