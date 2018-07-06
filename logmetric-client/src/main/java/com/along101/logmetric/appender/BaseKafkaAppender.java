package com.along101.logmetric.appender;

import static ch.qos.logback.core.CoreConstants.CODES_URL;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

import java.util.Properties;

import com.along101.logmetric.common.Constants;
import com.along101.logmetric.common.bean.LogEvent;
import com.along101.logmetric.config.LogMetricConfigManager;
import com.along101.logmetric.core.DataBufferWrapper;

import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.AppenderAttachable;
import com.along101.logmetric.encode.LogEventEncoder;

public abstract class BaseKafkaAppender<E> extends UnsynchronizedAppenderBase<E> implements AppenderAttachable<E> {

	protected Encoder<E> encoder;
	protected Properties setting = null;
	protected String topic = Constants.DEFAULT_TOPIC;
	protected String appid = null;

	static {
		DataBufferWrapper.getInstance().registerEncoder(LogEvent.class, new LogEventEncoder());
	}

	public Properties getSetting() {
		if (this.setting == null) {
			this.setting = LogMetricConfigManager.instance().getDefaultKafkaProperties();
		}
		return this.setting;
	}

	public void setLayout(Layout<E> layout) {
		addWarn("This appender no longer admits a layout as a sub-component, set an encoder instead.");
		addWarn("To ensure compatibility, wrapping your layout in LayoutWrappingEncoder.");
		addWarn("See also " + CODES_URL + "#layoutInsteadOfEncoder for details");
		LayoutWrappingEncoder<E> lwe = new LayoutWrappingEncoder<E>();
		lwe.setLayout(layout);
		lwe.setContext(context);
		this.encoder = lwe;
	}
	
    protected boolean checkPrerequisites() {
        boolean errorFree = true;

        if (setting.get(BOOTSTRAP_SERVERS_CONFIG) == null) {
            addError("No bootstrap.servers set for the appender named [\""
                    + name + "\"].");
            errorFree = false;
        }

        if (topic == null) {
            addError("No topic set for the appender named [\"" + name + "\"].");
            errorFree = false;
        }

        if (appid == null) {
            addError("No appid set for the appender named [\"" + name + "\"].");
            errorFree = false;
        }

        return errorFree;
    }

	public Encoder<E> getEncoder() {
		return encoder;
	}

	public void setEncoder(Encoder<E> encoder) {
		this.encoder = encoder;
	}

	public void addSettings(String keyValue) {
		String[] split = keyValue.split("=", 2);
		if (split.length == 2) {
			putKafkaSetting(split[0], split[1]);
		}
	}
    
	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
		LogMetricConfigManager.instance().setAppId(appid);
	}

	public void putKafkaSetting(String key, String value) {
		if (Constants.LogTopic.equals(key.trim())) {
			this.topic = value.trim();
		} else {
			getSetting().put(key, value);
		}
	}

}
