package ploiu.test

import ploiu.gameLibrary.event.annotation.SubscribeEvent

class TestSubscribeEventClassKotlin {
    @SubscribeEvent(TestEvent::class)
    fun test(event: TestEvent) {

    }
}