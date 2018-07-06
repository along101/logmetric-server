package com.along101.logmetric.common.bean;

import com.google.common.base.Strings;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by yinzuolong on 2017/3/10.
 */
@Setter
@Getter
public class Message {
	
	private String appId;

	private long sendTime;
	
	private long createTime;

	private MessageType type;
	
	private Header header;
	
	private int dataCount;
	
	private byte[] payload;
	
	public Message(String appId){
		this.appId = appId;
	}
	
}
