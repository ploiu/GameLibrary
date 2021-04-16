package ploiu.gameLibrary

import org.junit.jupiter.api.assertThrows
import ploiu.gameLibrary.event.DynamicEventHandler
import ploiu.gameLibrary.event.EventRegistry
import ploiu.gameLibrary.event.GameEvent
import ploiu.gameLibrary.event.annotation.EventHandler
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
        EventRegistry.registerStaticEventHandlers(*classes.toTypedArray())
        assertEquals(2, EventRegistry.staticEventHandlers[TestEvent::class.java]?.size)
    }

    @Test
    @Suppress("UNUSED_PARAMETER", "UNUSED")
    fun testRegisterStaticEventHandlersThrowsExceptionForInvalidHandler() {
        class BadHandler {
            @EventHandler(TestEvent::class)
            fun handleTest(e: String) {
            }
        }
        assertThrows<TerminalInvalidEventHandlerException> {
            EventRegistry.registerStaticEventHandlers(BadHandler::class)
        }
    }

    @Test
    fun testDynamicRegisterEventHandler() {
        EventRegistry.registerEventHandler(DynamicEventHandler(TestEvent::class) {})
        assertEquals(1, EventRegistry.dynamicEventHandlers[TestEvent::class.java]?.size)
    }

    @Test
    fun testDynamicRegisterEventHandlerThrowsExceptionIfHandlerIsBad() {
        assertThrows<InvalidEventHandlerException> {
            EventRegistry.registerEventHandler(DynamicEventHandler(GameEvent::class) {})
        }
    }
}