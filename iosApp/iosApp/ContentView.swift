import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea() // Critical for edge-to-edge tactical immersion
            .background(Color(red: 2/255, green: 6/255, blue: 23/255)) // Match #020617 Ebony
    }
}



