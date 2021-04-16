import ploiu.gameLibrary.Ploiu
import ploiu.gameLibrary.WindowFlags
import ploiu.gameLibrary.event.DynamicEventHandler
import ploiu.gameLibrary.event.EventRegistry
import ploiu.gameLibrary.event.GameEvent
import ploiu.gameLibrary.event.GameExitEvent
import ploiu.gameLibrary.event.annotation.EventHandler
import ploiu.gameLibrary.initGameWindow
import java.awt.Color
import java.awt.Dimension
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.exitProcess

/**
 * Simple test file for testing things. Won't be included in the actual code (TODO add to gitignore)
 */
fun main() {
    val options = WindowFlags.DOUBLE_BUFFERED
    initGameWindow("test", Dimension(500, 500), options)
    EventRegistry.registerStaticEventHandlers(EventHandlerTest::class, Test::class)
    val window = Ploiu.gameWindow!!
    val graphics = window.graphics
    graphics.color = Color.RED
    graphics.fillRect(0, 0, 500, 500)
    window.isVisible = true
    window.render()
    EventRegistry.registerEventHandler(DynamicEventHandler(GameExitEvent::class) {
        println((it as GameExitEvent).window)
    })
    class TestEvent : GameEvent()

    val atomicXInt = AtomicInteger(0)
    val atomicYInt = AtomicInteger(0)
    val x = Thread {
        while (true) {
            println("About to add an event from thread x")
            atomicXInt.incrementAndGet()
            Ploiu.postEvent(TestEvent())
            Thread.sleep(10)
        }
    }
    val y = Thread {
        while (true) {
            println("About to add an event from thread y")
            atomicYInt.incrementAndGet()
            Ploiu.postEvent(TestEvent())
            Thread.sleep(5)
        }
    }

    x.start()
    y.start()
    while (true) {
        Ploiu.processEvents()
        for (event in Ploiu.events) {
            if (event is GameExitEvent) {
                exitProcess(0)
            }
        }
        Ploiu.tick(20)
        Ploiu.clearEventPool()
        atomicXInt.set(0)
        atomicYInt.set(0)
    }


}

@Suppress("UNUSED_PARAMETER", "UNUSED")
class EventHandlerTest {
    companion object {
        @EventHandler(GameExitEvent::class)
        @JvmStatic
        fun onGameExit(event: GameExitEvent) {
            println("test")
        }
    }
}