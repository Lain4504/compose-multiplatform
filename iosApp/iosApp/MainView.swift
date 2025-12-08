import SwiftUI

struct MainView: View {
    @State private var selectedTab = 0
    
    var body: some View {
        TabView(selection: $selectedTab) {
            NavigationStack {
                HomeView()
            }
            .tabItem {
                Label("Home", systemImage: "house.fill")
            }
            .tag(0)
            
            NavigationStack {
                CounterView()
            }
            .tabItem {
                Label("Counter", systemImage: "number")
            }
            .tag(1)
            
            NavigationStack {
                CalculatorView()
            }
            .tabItem {
                Label("Calculator", systemImage: "function")
            }
            .tag(2)
            
            NavigationStack {
                TodoListView()
            }
            .tabItem {
                Label("Todos", systemImage: "checklist")
            }
            .tag(3)
            
            NavigationStack {
                TimerView()
            }
            .tabItem {
                Label("Timer", systemImage: "stopwatch")
            }
            .tag(4)
            
            NavigationStack {
                NotesView()
            }
            .tabItem {
                Label("Notes", systemImage: "note.text")
            }
            .tag(5)
            
            NavigationStack {
                TaskListView()
            }
            .tabItem {
                Label("Tasks", systemImage: "list.bullet.clipboard")
            }
            .tag(6)
        }
    }
}

#Preview {
    MainView()
}

