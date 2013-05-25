package com.socialmanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

public class RenrenInfoKeeper {
	private static final String PREFERENCES_NAME = "com_renren_sdk_android";
	
	public static void keepAccessToken(Context context, Bundle values) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("token", values.getString("access_token"));
		editor.putString("expiresTime", values.getString("expires_in"));
		editor.commit();
	}
	
	/**
	 * 清空sharepreference
	 * @param context
	 */
	public static void clear(Context context){
	    SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
	    Editor editor = pref.edit();
	    editor.clear();
	    editor.commit();
	}
	
	public static Bundle readAccessToken(Context context){
		Bundle values = new Bundle();
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		values.putString("access_token", pref.getString("token", ""));
		values.putString("expires_in", pref.getString("expiresTime", ""));
		return values;
	}
}
