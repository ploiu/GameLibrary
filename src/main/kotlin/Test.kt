import ploiu.gameLibrary.Ploiu
import ploiu.gameLibrary.WindowFlags
import ploiu.gameLibrary.event.GameExitEvent
import ploiu.gameLibrary.event.annotation.SubscribeEvent
import ploiu.gameLibrary.initGameWindow
import java.awt.Color
import java.awt.Dimension

fun main() {
    val options = WindowFlags.DOUBLE_BUFFERED
    initGameWindow("test", Dimension(500, 500), options)
    Ploiu.registerStaticEventHandlers(EventHandlerTest::class)
    val window = Ploiu.gameWindow
    if (window != null) {
        val graphics = window.graphics
        graphics.color = Color.RED
        graphics.fillRect(0, 0, 500, 500)
        window.isVisible = true
        window.render()
    }
    while (true) {
        
    }
}

class EventHandlerTest {
    @SubscribeEvent(GameExitEvent::class)
    fun onGameExit(event: GameExitEvent) {
        println("test")
    }
}