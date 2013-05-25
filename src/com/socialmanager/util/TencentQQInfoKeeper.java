package com.socialmanager.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
 
public class TencentQQInfoKeeper {
	private static final String PREFERENCES_NAME = "com_tecent_qq_sdk_android";
	
	public static void keepAccessToken(Context context, JSONObject json) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		try {
			editor.putString("openid", json.getString("openid"));
			editor.putString("token", json.getString("access_token"));
			editor.putString("expiresTime", json.getString("expires_in"));
			editor.commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Clear sharepreference
	 * @param context
	 */
	public static void clear(Context context){
	    SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
	    Editor editor = pref.edit();
	    editor.clear();
	    editor.commit();
	}
	
	public static JSONObject readAccessToken(Context context){
		JSONObject json = new JSONObject();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		if (pref.contains("token")) {
			try {
				json.put("openid", pref.getString("openid", ""));
				json.put("access_token", pref.getString("token", ""));
				json.put("expires_in", pref.getString("expiresTime", ""));
				return json;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
}