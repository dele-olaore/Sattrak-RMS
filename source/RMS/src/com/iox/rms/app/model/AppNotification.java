package com.iox.rms.app.model;

import java.io.Serializable;

public class AppNotification implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String type; // INFO, ERROR
	private String subject;
	private String message;
	
	private boolean showed = false;
	
	public AppNotification()
	{}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isShowed() {
		return showed;
	}

	public void setShowed(boolean showed) {
		this.showed = showed;
	}
	
}
