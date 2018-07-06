package com.along101.logmetric.client;

import java.util.Map;
import java.util.Random;

import com.along101.logmetric.common.IMetric;
import com.google.common.collect.Maps;

public class KafkaMetricTest {

    public static void main(String args[]) {
        IMetric metric = MetricFactory.getInstance().getMetric();
        Map<String, String> tags = Maps.newHashMap();
        tags.put("aa", "sss");
        int i = 0;
        while (true) {
//            SleepUtil.sleep(20);

            metric.log("test.metric", tags, new Random().nextInt(20));

            if (i % 100 == 0) {
                System.out.println("producer:" + (i));
            }
            i++;
        }
    }

}
