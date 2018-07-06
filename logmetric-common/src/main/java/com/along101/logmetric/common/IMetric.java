package com.along101.logmetric.common;

import java.util.Map;

import com.along101.logmetric.common.bean.Metric;

public interface IMetric {

	void log(String name,  Map<String, String> tags, int value);

	void log(String name,  Map<String, String> tags, long time, int value);

	void log(String name, Map<String, String> tags, long value);

	void log(String name,  Map<String, String> tags, long time, long value);

	void log(String name, Map<String, String> tags, double value);

	void log(String name,  Map<String, String> tags, long time, double value);

	void log(String name,  Map<String, String> tags, float value);

	void log(String name, Map<String, String> tags, long time, float value);
	
	void addMetric(Metric metric);
}
