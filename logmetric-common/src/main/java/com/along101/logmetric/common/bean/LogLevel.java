package com.along101.logmetric.common.bean;

import lombok.Getter;

@Getter
public enum LogLevel {
	ALL(0,"ALL"),
    TRACE(1,"TRACE"),
    DEBUG(2,"DEBUG"),
    INFO(3,"INFO"),
    WARN(4,"WARN"),
    ERROR(5,"ERROR"),
	OFF(6,"OFF");
	private int code;
	private String name;
	
	LogLevel(int code,String name){
		this.code = code;
		this.name = name;
	}
	
	public static LogLevel getLogLevelByName(String name){
		for(LogLevel level:values()){
			if(level.getName().equals(name)){
				return level;
			}
		}
		return INFO;
	}
}
