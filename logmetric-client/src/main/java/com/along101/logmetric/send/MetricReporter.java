package com.along101.logmetric.send;

import com.along101.logmetric.agent.AgentFactory;
import com.along101.logmetric.common.IAgent;
import com.along101.logmetric.common.IReporter;
import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.bean.MessageType;
import com.along101.logmetric.common.bean.StorageType;
import com.along101.logmetric.config.LogMetricConfigManager;

import java.util.Properties;

public class MetricReporter implements IReporter {

    private IAgent agent;

    public MetricReporter() {
        StorageType storageType = LogMetricConfigManager.instance().getMetricStorageType();
        Properties props = LogMetricConfigManager.instance().getKafkaProperties();
        props.setProperty("bootstrap.servers", LogMetricConfigManager.instance().getMetricServer());
        this.agent = AgentFactory.instance().getAgent(storageType, props);
        this.agent.init(LogMetricConfigManager.instance().getMetricTopic(), "600000");
    }

    @Override
    public void process(Message msg) {
        if (msg.getType().ordinal() == MessageType.METRIC.ordinal()) {
            agent.send(msg);
        }
    }
}
