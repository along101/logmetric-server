package com.along101.logmetric.client;

import com.along101.logmetric.config.LogMetricConfigManager;

public class ConfigTest {

	public static void main(String args[]) {
		System.out.println(LogMetricConfigManager.instance().getMetricConfig());
	}
}
