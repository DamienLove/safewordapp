    @State private var isListening = false
    var body: some View {
        VStack {
            Text("SafeWord Listening: \(isListening ? "Active" : "Inactive")")
            Button("Start Listening") {
                isListening = true
            }
        }
    }