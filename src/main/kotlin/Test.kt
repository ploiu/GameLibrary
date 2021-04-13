import ploiu.gameLibrary.Ploiu
import ploiu.gameLibrary.WindowFlags
import ploiu.gameLibrary.event.Applicator
import ploiu.gameLibrary.event.DynamicEventHandler
import ploiu.gameLibrary.event.GameExitEvent
import ploiu.gameLibrary.event.annotation.SubscribeEvent
import ploiu.gameLibrary.initGameWindow
import java.awt.Color
import java.awt.Dimension
import kotlin.system.exitProcess

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
    Ploiu.registerEventHandler(DynamicEventHandler(GameExitEvent::class) {
        println((it as GameExitEvent).window)
    })
    while (true) {
        Ploiu.processEvents()
        for (event in Ploiu.events) {
            if (event is GameExitEvent) {
                exitProcess(0)
            }
        }
        Ploiu.tick(20)
    }
}

@Suppress("UNUSED_PARAMETER", "UNUSED")
class EventHandlerTest {
    companion object {
        @SubscribeEvent(GameExitEvent::class)
        @JvmStatic
        fun onGameExit(event: GameExitEvent) {
            println("test")
        }
    }
}