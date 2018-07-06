package com.along101.logmetric.client;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogBackTest {

	public static void main(String args[]) throws InterruptedException {
		int i = 0;
		while (i++ < 10000) {
			try {
				//log.error("fsafasfas", new RuntimeException("RuntimeException"));

				log.error("feiyongjunssss", "--------------");
			} catch (Throwable t) {
				//log.error("fsafasfas", t);
			}
		}
		Thread.sleep(100000);
		System.out.println("fsfas");
	}
}
