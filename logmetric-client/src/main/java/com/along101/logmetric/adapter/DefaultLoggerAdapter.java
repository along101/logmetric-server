package com.along101.logmetric.adapter;

import java.util.Date;

import com.along101.logmetric.common.ILogger;
import com.along101.logmetric.common.bean.LogEvent;
import com.along101.logmetric.common.bean.LogLevel;
import com.along101.logmetric.config.LogMetricConfigManager;
import com.along101.logmetric.core.DataBufferWrapper;
import com.along101.logmetric.core.MDC;
import com.along101.logmetric.encode.LogEventEncoder;
import com.dianping.cat.Cat;
import com.dianping.cat.message.spi.MessageTree;

public class DefaultLoggerAdapter implements ILogger {

	protected String name;
	 
	private LogLevel level;
	
	public DefaultLoggerAdapter(String name){
		this.name = name;
		DataBufferWrapper.getInstance().registerEncoder(LogEvent.class, new LogEventEncoder());
		this.level = LogMetricConfigManager.instance().getLogLevel();
	}
	
	@Override
	public String getName() {
		return this.name;
	}

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void trace(String msg) {
        writeLog(name, LogLevel.TRACE, msg);
    }

    @Override
    public void trace(String format, Object arg) {
        writeLog(name,LogLevel.TRACE,String.format(format,arg));
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        writeLog(name,LogLevel.TRACE,String.format(format,arg1,arg2));
    }

    @Override
    public void trace(String format, Object... arguments) {
        writeLog(name,LogLevel.TRACE,String.format(format,arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
        writeLog(name,LogLevel.TRACE,msg,t);
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public void debug(String msg) {
		writeLog(name,LogLevel.DEBUG,msg);
    }

    @Override
    public void debug(String format, Object arg) {
        writeLog(name,LogLevel.DEBUG,String.format(format,arg));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        writeLog(name,LogLevel.DEBUG,String.format(format,arg1,arg2));
    }

    @Override
    public void debug(String format, Object... arguments) {
        writeLog(name,LogLevel.DEBUG,String.format(format,arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
        writeLog(name,LogLevel.DEBUG,msg,t);
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String msg) {
         writeLog(name,LogLevel.INFO,msg);
    }

    @Override
    public void info(String format, Object arg) {
         writeLog(name,LogLevel.INFO,String.format(format,arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
         writeLog(name,LogLevel.INFO,String.format(format,arg1,arg2));
    }

    @Override
    public void info(String format, Object... arguments) {
         writeLog(name,LogLevel.INFO,String.format(format,arguments));
    }

    @Override
    public void info(String msg, Throwable t) {
         writeLog(name,LogLevel.INFO,msg,t);
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void warn(String msg) {
         writeLog(name,LogLevel.WARN,msg);
    }

    @Override
    public void warn(String format, Object arg) {
         writeLog(name,LogLevel.WARN,String.format(format,arg));
    }

    @Override
    public void warn(String format, Object... arguments) {
         writeLog(name,LogLevel.WARN,String.format(format,arguments));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
         writeLog(name,LogLevel.WARN,String.format(format,arg1,arg2));
    }

    @Override
    public void warn(String msg, Throwable t) {
       writeLog(name,LogLevel.WARN,msg,t);
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public void error(String msg) {
        writeLog(name,LogLevel.ERROR,msg);
    }

    @Override
    public void error(String format, Object arg) {
        writeLog(name,LogLevel.ERROR,String.format(format,arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        writeLog(name,LogLevel.ERROR,String.format(format,arg1,arg2));
    }

    @Override
    public void error(String format, Object... arguments) {
        writeLog(name,LogLevel.ERROR,String.format(format,arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
       writeLog(name,LogLevel.ERROR,msg,t);
    }

    private void writeLog(String name,LogLevel level,String msg) {
        writeLog(name,level,msg,null);
    }
    
    private  void writeLog(String name,LogLevel level,String msg,Throwable throwable) {
        if (level.equals(LogLevel.ERROR)) {
            if (throwable == null) {
                throwable = new Throwable(msg);
            }
            Cat.logError(msg, throwable);
        }
        LogEvent logEvent = new LogEvent();
        logEvent.setExceptionItem(throwable);
        logEvent.setLevel(level);
        logEvent.setLogName(name);
        logEvent.setMessage(msg);
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

        writeLog(logEvent);
    }
    
    private void writeLog(LogEvent logEvent){
    	if(logEvent.getLevel().getCode() >= this.level.getCode()){
    		DataBufferWrapper.getInstance().add(logEvent);
    	}
    }
}
