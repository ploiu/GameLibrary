package ploiu.test

import ploiu.gameLibrary.event.annotation.SubscribeEvent

@Suppress("UNUSED_PARAMETER", "UNUSED")
class TestSubscribeEventClassKotlin {
    companion object {
        @JvmStatic
        @SubscribeEvent(TestEvent::class)
        fun test(event: TestEvent) {
        }
    }
}