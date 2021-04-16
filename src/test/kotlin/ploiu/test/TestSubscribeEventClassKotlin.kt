package ploiu.test

import ploiu.gameLibrary.event.annotation.EventHandler

@Suppress("UNUSED_PARAMETER", "UNUSED")
class TestSubscribeEventClassKotlin {
    companion object {
        @JvmStatic
        @EventHandler(TestEvent::class)
        fun test(event: TestEvent) {
        }
    }
}