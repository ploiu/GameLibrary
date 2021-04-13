package ploiu.gameLibrary

import org.junit.jupiter.api.assertThrows
import ploiu.gameLibrary.event.DynamicEventHandler
import ploiu.gameLibrary.event.GameEvent
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
        assertEquals(2, Ploiu.staticEventHandlers[TestEvent::class.java]?.size)
    }

    @Test
    @Suppress("UNUSED_PARAMETER", "UNUSED")
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
        Ploiu.registerEventHandler(DynamicEventHandler(TestEvent::class) {})
        assertEquals(1, Ploiu.dynamicEventHandlers[TestEvent::class.java]?.size)
    }

    @Test
    fun testDynamicRegisterEventHandlerThrowsExceptionIfHandlerIsBad() {
        assertThrows<InvalidEventHandlerException> {
            Ploiu.registerEventHandler(DynamicEventHandler(GameEvent::class) {})
        }
    }
}