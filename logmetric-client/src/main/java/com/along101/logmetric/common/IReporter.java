package com.along101.logmetric.common;

import com.along101.logmetric.common.bean.Message;

public interface IReporter {
	void process(Message msg);
}
