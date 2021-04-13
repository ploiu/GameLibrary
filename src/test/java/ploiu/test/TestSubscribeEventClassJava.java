package ploiu.test;

import ploiu.gameLibrary.event.annotation.SubscribeEvent;

public class TestSubscribeEventClassJava {
	@SubscribeEvent(TestEvent.class)
	public static void test(TestEvent e) {
	}
}
