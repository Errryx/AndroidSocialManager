package com.socialmanager.model;

import java.io.Serializable;

public class AccessToken implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7253915024915070790L;
	
	private String access_token = null;
	private String remind_in = null;
	private String expires_in = null;
	private String uid = null;
	
	public String getAccess_token() {
		return access_token;
	}
	
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	
	public String getRemind_in() {
		return remind_in;
	}
	
	public void setRemind_in(String remind_in) {
		this.remind_in = remind_in;
	}
	
	public String getExpires_in() {
		return expires_in;
	}
	
	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}
	
	public String getUid() {
		return uid;
	}
	
	public void setUid(String uid) {
		this.uid = uid;
	}
			
}
