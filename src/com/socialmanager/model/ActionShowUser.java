package com.socialmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = {"idstr", "name", "province", "city", "location", 
		"description", "url", "profile_url", "domain",  "weihao", "gender",	"followers_count", "friends_count", 
		"statuses_count", "favourites_count", "created_at", "remark", "verified_reason", "lang",
		"following", "allow_all_act_msg", "geo_enabled", "verified", "allow_all_comment", "follow_me",
		"verified_type", "online_status", "bi_followers_count", "star", "mbtype", "mbrank", "block_word"})
public class ActionShowUser {
	private long id = -1L;
	private String screen_name = null;
	private String avatar_large = null;
	private String profile_image_url = null;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getScreen_name() {
		return screen_name;
	}
	public void setScreen_name(String screen_name) {
		this.screen_name = screen_name;
	}
	public String getAvatar_large() {
		return avatar_large;
	}
	public void setAvatar_large(String avatar_large) {
		this.avatar_large = avatar_large;
	}
	public String getProfile_image_url() {
		return profile_image_url;
	}
	public void setProfile_image_url(String profile_image_url) {
		this.profile_image_url = profile_image_url;
	}
}
