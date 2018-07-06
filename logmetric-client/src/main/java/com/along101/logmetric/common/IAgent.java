package com.along101.logmetric.common;

import com.along101.logmetric.common.bean.Message;

public interface IAgent {
	void init(String ... configs);
	void send(Message msg);
}
