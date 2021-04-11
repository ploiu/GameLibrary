package ploiu.gameLibrary

import org.junit.jupiter.api.assertThrows
import ploiu.gameLibrary.event.GameExitEvent
import ploiu.gameLibrary.event.annotation.SubscribeEvent
import ploiu.gameLibrary.exception.InvalidEventHandlerException
import ploiu.gameLibrary.exception.TerminalInvalidEventHandlerException
import ploiu.test.TestEvent
import ploiu.test.TestSubscribeEventClassJava
import ploiu.test.TestSubscribeEventClassKotlin
import kotlin.test.Test
import kotlin.test.assertEquals

class CoreTests {
    @Test
    fun testRegisterStaticEventHandlersRegistersBothJavaAndKotlinEventHandlers() {
        val classes =
            listOf<Class<*>>(TestSubscribeEventClassJava::class.java, TestSubscribeEventClassKotlin::class.java)
        Ploiu.registerStaticEventHandlers(*classes.toTypedArray())
        assertEquals(2, Ploiu.eventHandlers[TestEvent::class.java]?.size)
    }

    @Test
    fun testRegisterStaticEventHandlersThrowsExceptionForInvalidHandler() {
        class BadHandler {
            @SubscribeEvent(TestEvent::class)
            fun handleTest(e: String) {
            }
        }
        assertThrows<TerminalInvalidEventHandlerException> {
            Ploiu.registerStaticEventHandlers(BadHandler::class)
        }
    }

    @Test
    fun testDynamicRegisterEventHandler() {
        Ploiu.registerEventHandler(TestEvent::class, ::eventHandler)
        assertEquals(1, Ploiu.eventHandlers[TestEvent::class.java]?.size)
    }

    @Test
    fun testDynamicRegisterEventHandlerThrowsExceptionIfHandlerIsBad() {
        assertThrows<InvalidEventHandlerException> {
            Ploiu.registerEventHandler(GameExitEvent::class, ::eventHandler)
        }
    }

    // kotlin reflect cannot introspect local functions, so it must be declared outside of its test method
    private fun eventHandler(event: TestEvent) {}
}