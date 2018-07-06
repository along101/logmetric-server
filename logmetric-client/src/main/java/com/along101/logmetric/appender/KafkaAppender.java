package com.along101.logmetric.appender;

import java.util.Date;
import java.util.Iterator;

import com.along101.logmetric.common.bean.LogEvent;
import com.along101.logmetric.common.bean.LogLevel;
import com.along101.logmetric.core.DataBufferWrapper;
import com.along101.logmetric.core.MDC;
import com.along101.logmetric.send.LogEventReporter;
import com.dianping.cat.Cat;
import com.dianping.cat.message.spi.MessageTree;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

public class KafkaAppender<E> extends BaseKafkaAppender<E> {
	private static final String KAFKA_LOGGER_PREFIX = "org.apache.kafka.clients";
	private final AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();

	public KafkaAppender() {
	}

	@Override
	public void start() {
		// only error free appenders should be activated
		if (!checkPrerequisites())
			return;

		super.start();
		DataBufferWrapper.getInstance().registerReporter(new LogEventReporter(this.topic, this.setting));
	}

	@Override
	public void stop() {
		super.stop();
		try {
			DataBufferWrapper.getInstance().close();
		} catch (Exception e) {
			addError("DataBufferWrapper close error for the appender named [\"" + name + "\"].", e);
		}
	}

	@Override
	public void addAppender(Appender<E> newAppender) {
		aai.addAppender(newAppender);
	}

	@Override
	public Iterator<Appender<E>> iteratorForAppenders() {
		return aai.iteratorForAppenders();
	}

	@Override
	public Appender<E> getAppender(String name) {
		return aai.getAppender(name);
	}

	@Override
	public boolean isAttached(Appender<E> appender) {
		return aai.isAttached(appender);
	}

	@Override
	public void detachAndStopAllAppenders() {
		aai.detachAndStopAllAppenders();
	}

	@Override
	public boolean detachAppender(Appender<E> appender) {
		return aai.detachAppender(appender);
	}

	@Override
	public boolean detachAppender(String name) {
		return aai.detachAppender(name);
	}

	@Override
	public void doAppend(E e) {
		if (e instanceof ILoggingEvent && ((ILoggingEvent) e).getLoggerName().startsWith(KAFKA_LOGGER_PREFIX)) {
			deferAppend(e);
		} else {
			super.doAppend(e);
		}
	}

	private void deferAppend(E event) {
		append(event);
	}

	@Override
	protected void append(E e) {

		try {
			LoggingEvent event = (LoggingEvent) e;
			LogEvent logEvent = new LogEvent();
			Throwable throwable = castThrowable(event.getThrowableProxy());
			if (event.getLevel().toInt() == Level.ERROR_INT) {
				if (throwable == null) {
					throwable = new Throwable(event.getMessage());
				}
				//Cat.logError(event.getMessage(), throwable);
			}
			logEvent.setExceptionItem(throwable);
			logEvent.setLevel(LogLevel.getLogLevelByName(event.getLevel().levelStr));
			logEvent.setLogName(event.getLoggerName());
			logEvent.setMessage(event.getFormattedMessage());
			logEvent.setTimeStamp(event.getTimeStamp());
			logEvent.setTimeStamp(new Date());
			logEvent.setTags(MDC.getCopyOfContextMap());
			
			MessageTree msgTree = Cat.getManager().getThreadLocalMessageTree();
			if (msgTree != null) {
				String msgId = msgTree.getMessageId();
				if (msgId == null) {
					msgId = Cat.getCurrentMessageId();
				}

				logEvent.putTag("TraceChain", String.format("http://cat.along101.com/cat/r/m/%s", msgId));
			}

			DataBufferWrapper.getInstance().add(logEvent);

		} catch (Throwable t) {
			addWarn(" DataBufferWrapper add error for the appender named [\"" + name + "\"].", t);
		}

	}

	private Throwable castThrowable(IThrowableProxy throwableProxy) {
		Throwable throwable = null;
		if (null != throwableProxy) {
			StackTraceElementProxy stackProxys[] = throwableProxy.getStackTraceElementProxyArray();
			StackTraceElement stackElements[] = new StackTraceElement[stackProxys.length];
			for (int i = 0; i < stackProxys.length; i++) {
				stackElements[i] = stackProxys[i].getStackTraceElement();
			}

			throwable = new Throwable(throwableProxy.getMessage());
			throwable.setStackTrace(stackElements);
		}
		return throwable;

	}

}
