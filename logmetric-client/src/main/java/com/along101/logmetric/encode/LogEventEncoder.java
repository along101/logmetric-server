package com.along101.logmetric.encode;

import com.along101.logmetric.common.IEncoder;
import com.along101.logmetric.common.bean.Header;
import com.along101.logmetric.common.bean.LogEvent;
import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.bean.MessageType;
import com.along101.logmetric.common.util.KryoUtil;
import com.along101.logmetric.config.LogMetricConfigManager;

import java.util.List;

public class LogEventEncoder implements IEncoder<LogEvent> {

    @Override
    public Message encode(List<LogEvent> list) {

        Message msg = new Message(LogMetricConfigManager.instance().getAppId());
        msg.setCreateTime(System.currentTimeMillis());
        msg.setType(MessageType.LOG);
        msg.setDataCount(list.size());
        msg.setHeader(new Header());
        msg.setPayload(KryoUtil.serialize(list));

        return msg;
    }

}
