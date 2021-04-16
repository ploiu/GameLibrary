package ploiu.test;

import ploiu.gameLibrary.event.annotation.EventHandler;

public class TestSubscribeEventClassJava {
	@EventHandler(TestEvent.class)
	public static void test(TestEvent e) {
	}
}
