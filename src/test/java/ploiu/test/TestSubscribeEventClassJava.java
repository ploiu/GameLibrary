package ploiu.test;

import ploiu.gameLibrary.event.annotation.SubscribeEvent;

public class TestSubscribeEventClassJava {
	@SubscribeEvent(TestEvent.class)
	public void test(TestEvent e) {
	}
}
