import SwiftUI
import Shared

struct CalculatorView: View {
    @StateObject private var viewModel = CalculatorViewModel()
    
    var body: some View {
        VStack(spacing: 24) {
            // Display
            VStack(spacing: 8) {
                Text("Calculator")
                    .font(.title2)
                    .fontWeight(.bold)
                
                Text(viewModel.display)
                    .font(.system(size: 36, weight: .bold, design: .monospaced))
                    .frame(maxWidth: .infinity, alignment: .trailing)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                    .lineLimit(1)
                    .minimumScaleFactor(0.5)
                
                if let error = viewModel.errorMessage {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                }
            }
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(16)
            .shadow(color: .black.opacity(0.1), radius: 5, x: 0, y: 2)
            
            // Buttons
            VStack(spacing: 12) {
                // Row 1: 7, 8, 9, C
                HStack(spacing: 12) {
                    CalcButton("7", isNumber: true) { viewModel.appendNumber("7") }
                    CalcButton("8", isNumber: true) { viewModel.appendNumber("8") }
                    CalcButton("9", isNumber: true) { viewModel.appendNumber("9") }
                    CalcButton("C", isNumber: false) { viewModel.clear() }
                }
                
                // Row 2: 4, 5, 6, +
                HStack(spacing: 12) {
                    CalcButton("4", isNumber: true) { viewModel.appendNumber("4") }
                    CalcButton("5", isNumber: true) { viewModel.appendNumber("5") }
                    CalcButton("6", isNumber: true) { viewModel.appendNumber("6") }
                    CalcButton("+", isNumber: false) { viewModel.appendOperator("+") }
                }
                
                // Row 3: 1, 2, 3, -
                HStack(spacing: 12) {
                    CalcButton("1", isNumber: true) { viewModel.appendNumber("1") }
                    CalcButton("2", isNumber: true) { viewModel.appendNumber("2") }
                    CalcButton("3", isNumber: true) { viewModel.appendNumber("3") }
                    CalcButton("-", isNumber: false) { viewModel.appendOperator("-") }
                }
                
                // Row 4: 0, ., =
                HStack(spacing: 12) {
                    CalcButton("0", isNumber: true) { viewModel.appendNumber("0") }
                        .frame(maxWidth: .infinity)
                    CalcButton(".", isNumber: true) { viewModel.appendNumber(".") }
                    CalcButton("=", isNumber: false) { viewModel.calculate() }
                }
                
                // Row 5: *, /, ^, √
                HStack(spacing: 12) {
                    CalcButton("×", isNumber: false) { viewModel.appendOperator("*") }
                    CalcButton("÷", isNumber: false) { viewModel.appendOperator("/") }
                    CalcButton("^", isNumber: false) { viewModel.appendOperator("^") }
                    CalcButton("√", isNumber: false) { viewModel.sqrt() }
                }
            }
            
            Text("Example: 5 + 3 or 2 ^ 3")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding()
        .navigationTitle("Calculator")
    }
}

struct CalcButton: View {
    let text: String
    let isNumber: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(text)
                .font(.title2)
                .fontWeight(.semibold)
                .frame(maxWidth: .infinity)
                .frame(height: 60)
                .background(isNumber ? Color(.systemGray5) : Color.orange)
                .foregroundColor(isNumber ? .primary : .white)
                .cornerRadius(12)
        }
    }
}

class CalculatorViewModel: ObservableObject {
    private let calculator = Calculator()
    @Published var display: String = "0"
    @Published var errorMessage: String? = nil
    
    func appendNumber(_ num: String) {
        errorMessage = nil
        if display == "0" {
            display = num
        } else {
            display += num
        }
    }
    
    func appendOperator(_ op: String) {
        errorMessage = nil
        display += " \(op) "
    }
    
    func clear() {
        display = "0"
        errorMessage = nil
    }
    
    func calculate() {
        do {
            let result = try calculator.calculate(expression: display)
            if result.truncatingRemainder(dividingBy: 1) == 0 {
                display = String(format: "%.0f", result)
            } else {
                display = String(format: "%.2f", result)
            }
            errorMessage = nil
        } catch {
            errorMessage = "Error: \(error.localizedDescription)"
        }
    }
    
    func sqrt() {
        if let num = Double(display) {
            do {
                let result = try calculator.sqrt(value: num)
                if result.truncatingRemainder(dividingBy: 1) == 0 {
                    display = String(format: "%.0f", result)
                } else {
                    display = String(format: "%.2f", result)
                }
                errorMessage = nil
            } catch {
                errorMessage = "Error: \(error.localizedDescription)"
            }
        }
    }
}

#Preview {
    NavigationStack {
        CalculatorView()
    }
}

