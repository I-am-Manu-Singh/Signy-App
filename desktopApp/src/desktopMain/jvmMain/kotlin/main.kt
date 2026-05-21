import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.myapplication.common.di.initKoin

fun main() {
    initKoin()
    application {
        Window(onCloseRequest = ::exitApplication, title = "Signy Tactical Desktop") {
            MainView()
        }
    }
}