package com.along101.logmetric.common.bean;

import lombok.Getter;

@Getter
public enum StorageType {
	Kafka(1, "kafka"), InfluxDB(2, "influxdb"), KairosDB(3, "kairosdb"), OpenTSDB(4, "opentsdb");
	private int code;
	private String name;

	StorageType(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public static StorageType getStorageTypeByName(String name) {

		for (StorageType type : values()) {
			if (type.name.equals(name)) {
				return type;
			}
		}
		return Kafka;
	}
}
