package com.along101.logmetric.client;

import java.util.HashMap;
import java.util.Map;

import com.along101.logmetric.common.ILogger;
import com.along101.logmetric.common.IMetric;
import com.along101.logmetric.core.MDC;

public class KafkaLogTest {

	public static void main(String args[]) {
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("tester", "wk");
		MDC.setContextMap(tags);
		ILogger logger = LoggerFactory.getLogger(KafkaLogTest.class);

		IMetric metric = MetricFactory.getInstance().getMetric();
		while (true) {
			logger.info("test");
			metric.log("test", tags, 1);

		}
	}
}
