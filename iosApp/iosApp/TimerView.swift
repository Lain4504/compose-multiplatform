import SwiftUI
import Shared

struct TimerView: View {
    @StateObject private var viewModel = TimerViewModel()
    
    var body: some View {
        VStack(spacing: 32) {
            VStack(spacing: 16) {
                Text("Stopwatch")
                    .font(.title2)
                    .fontWeight(.bold)
                
                Text(viewModel.formattedTime)
                    .font(.system(size: 64, weight: .bold, design: .monospaced))
                    .foregroundColor(.blue)
                    .padding()
                    .frame(minWidth: 200)
                    .background(Color(.systemGray6))
                    .cornerRadius(16)
                
                Text("Status: \(viewModel.status)")
                    .font(.headline)
                    .foregroundColor(.secondary)
                
                HStack(spacing: 16) {
                    Button(action: {
                        viewModel.toggleStartPause()
                    }) {
                        Label(
                            viewModel.isRunning ? "Pause" : "Start",
                            systemImage: viewModel.isRunning ? "pause.fill" : "play.fill"
                        )
                        .frame(maxWidth: .infinity)
                        .padding()
                        .background(viewModel.isRunning ? Color.orange : Color.green)
                        .foregroundColor(.white)
                        .cornerRadius(12)
                    }
                    
                    Button(action: {
                        viewModel.reset()
                    }) {
                        Label("Reset", systemImage: "arrow.counterclockwise")
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.red)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                    }
                }
            }
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(16)
            .shadow(color: .black.opacity(0.1), radius: 10, x: 0, y: 5)
            
            Text("This timer uses shared Kotlin business logic")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding()
        .navigationTitle("Timer")
    }
}

class TimerViewModel: ObservableObject {
    private let timer = Timer()
    @Published var formattedTime: String = "00:00"
    @Published var status: String = "Stopped"
    @Published var isRunning: Bool = false
    
    private var updateTimer: Timer?
    
    init() {
        formattedTime = timer.formatTime()
        status = timer.getStatus()
    }
    
    func toggleStartPause() {
        if isRunning {
            timer.pause()
            isRunning = false
        } else {
            timer.start()
            isRunning = true
        }
        updateDisplay()
        startUpdateTimer()
    }
    
    func reset() {
        timer.reset()
        isRunning = false
        updateDisplay()
        stopUpdateTimer()
    }
    
    private func updateDisplay() {
        formattedTime = timer.formatTime()
        status = timer.getStatus()
    }
    
    private func startUpdateTimer() {
        stopUpdateTimer()
        if isRunning {
            updateTimer = Timer.scheduledTimer(withTimeInterval: 0.1, repeats: true) { [weak self] _ in
                self?.updateDisplay()
            }
        }
    }
    
    private func stopUpdateTimer() {
        updateTimer?.invalidate()
        updateTimer = nil
    }
    
    deinit {
        stopUpdateTimer()
    }
}

#Preview {
    NavigationStack {
        TimerView()
    }
}

