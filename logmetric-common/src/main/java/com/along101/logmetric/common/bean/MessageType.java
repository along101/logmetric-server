package com.along101.logmetric.common.bean;

import lombok.Getter;

@Getter
public enum MessageType {
	METRIC("metric", 1), LOG("log", 2);

	private String name;
	private int code;

	MessageType(String name, int code) {
		this.name = name;
		this.code = code;
	}

	public static MessageType getMessageTypeByName(String name) {

		for (MessageType type : values()) {
			if (type.name.equals(name)) {
				return type;
			}
		}
		return LOG;
	}
}
