package com.along101.logmetric.config;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.along101.logmetric.common.Constants;
import com.along101.logmetric.common.bean.LogLevel;
import com.along101.logmetric.common.bean.StorageType;
import com.along101.logmetric.common.util.ConfigUtil;
import com.along101.logmetric.common.util.XMLUtil;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogMetricConfigManager {

	private static LogMetricConfigManager instance;

	private MetricConfig metricConfig;

	private LogConfig logConfig;

	private SettingConfig settingConfig;

	private String appId;

	private LogMetricConfigManager() {
	}

	public static LogMetricConfigManager instance() {
		if (instance == null) {
			synchronized (LogMetricConfigManager.class) {
				if (instance == null) {
					instance = new LogMetricConfigManager();
					instance.loadConfig();
				}
			}
		}
		return instance;
	}

	private void loadConfig() {

		String logmetricfile = Constants.XmlLogMetric;
		try {
			String env = System.getProperty("archaius.deployment.environment", null);
			if (env != null) {
				logmetricfile = "logmetric-" + env.trim() + ".xml";
			}

			LogMetricRootConfig logMetricRootConfig = XMLUtil.loadFromResource(LogMetricRootConfig.class,
					logmetricfile);

			this.appId = logMetricRootConfig.getLogMetricConfig().getAppid();
			this.logConfig = logMetricRootConfig.getLogMetricConfig().getLogConfig();
			this.metricConfig = logMetricRootConfig.getLogMetricConfig().getMetricConfig();
			this.settingConfig = logMetricRootConfig.getLogMetricConfig().getSettingConfig();
		} catch (Exception e) {
			// throw new RuntimeException("logmetric.xml parse error!");
			log.warn("has not found "+logmetricfile);
		}
	}

	public MetricConfig getMetricConfig() {
		return this.metricConfig;
	}

	public LogConfig getLogConfig() {
		return this.logConfig;
	}

	public SettingConfig getSettingConfig() {
		return this.settingConfig;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public int getBufferSize() {
		if (null != this.settingConfig) {
			return this.settingConfig.getBufferSize();
		} else {
			return Constants.DEFAULT_BUFFER_SIZE;
		}
	}

	public int getFetchSize() {
		if (null != this.settingConfig) {
			return this.settingConfig.getFetchSize();
		} else {
			return Constants.DEFAULT_FETCH_SIZE;
		}
	}

	public long getFetchTimeout() {
		if (null != this.settingConfig) {
			return this.settingConfig.getFetchTimeout();
		} else {
			return Constants.DEFAULT_FETCH_TIMEOUT;
		}
	}

	public int getWorkerSize() {
		if (null != this.settingConfig) {
			return this.settingConfig.getWorkerSize();
		} else {
			return Constants.DEFAULT_WORKER_SIZE;
		}
	}

	public int getWorkerBufferSize() {
		if (null != this.settingConfig) {
			return this.settingConfig.getWorkerBufferSize();
		} else {
			return Constants.DEFAULT_WORKER_BUFFER_SIZE;
		}
	}

	public String getMetricTopic() {
		if (this.metricConfig != null) {
			String topic = this.metricConfig.getConfigs().get(Constants.MetricTopic);
			if (!Strings.isNullOrEmpty(topic)) {
				return topic;
			}
		}

		return getDefaultTopic();
	}

	public String getMetricServer() {
		return this.metricConfig.getServer();
	}

	public String getLogServer() {
		return this.logConfig.getServer();
	}

	public LogLevel getLogLevel() {
		if (this.logConfig != null) {
			return LogLevel.getLogLevelByName(this.logConfig.getLevel());
		}
		return LogLevel.TRACE;
	}

	public StorageType getMetricStorageType() {
		if (this.metricConfig != null) {
			return StorageType.getStorageTypeByName(this.metricConfig.getStorageType());
		}

		return StorageType.Kafka;
	}

	public String getLogTopic() {
		if (null != this.logConfig) {
			String topic = this.logConfig.getConfigs().get(Constants.LogTopic);
			if (!Strings.isNullOrEmpty(topic)) {
				return topic;
			}
		}
		return getDefaultTopic();
	}

	public String getDefaultTopic() {
		if (null != this.settingConfig) {
			String topic = this.settingConfig.getTopic();
			if (!Strings.isNullOrEmpty(topic)) {
				return topic;
			}
		}
		return Constants.DEFAULT_TOPIC;
	}

	public Properties getKafkaProperties() {
		Properties props = getDefaultKafkaProperties();
		if (null != this.settingConfig) {
			Map<String, String> map = this.settingConfig.getConfigs();
			for (Entry<Object, Object> entry : props.entrySet()) {
				String key = entry.getKey().toString();
				if (map.containsKey(key)) {
					props.put(key, map.get(key));
				}
			}
		}

		return props;
	}

	public Properties getDefaultKafkaProperties() {
		return ConfigUtil.getProperties(Constants.LogMetricKafka);
	}
}
