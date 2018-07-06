package com.along101.logmetric.encode;

import java.util.List;

import com.along101.logmetric.common.IEncoder;
import com.along101.logmetric.common.bean.Header;
import com.along101.logmetric.common.bean.Message;
import com.along101.logmetric.common.bean.MessageType;
import com.along101.logmetric.common.bean.Metric;
import com.along101.logmetric.common.util.KryoUtil;
import com.along101.logmetric.config.LogMetricConfigManager;

public class MetricEncoder implements IEncoder<Metric> {
	
	public MetricEncoder(){
		
	}

	@Override
	public Message encode(List<Metric> list) {

		Message msg = new Message(LogMetricConfigManager.instance().getAppId());
		msg.setCreateTime(System.currentTimeMillis());
		msg.setType(MessageType.METRIC);
		msg.setDataCount(list.size());
		msg.setHeader(new Header());
		msg.setPayload(KryoUtil.serialize(list));

		return msg;
	}

	
}
