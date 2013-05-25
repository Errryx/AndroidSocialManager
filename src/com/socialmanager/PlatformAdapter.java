package com.socialmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Adapter to uniform different platforms
 * 
 * @author eryx.kao@gmail.com
 *
 */
public abstract class PlatformAdapter {
	
	/**
	 * Context to retrieve
	 */
	protected Context context = null;
	
	/**
	 * Listener for callback actions
	 */
	protected SocialActionListener listener = null;
	
	/**
	 * Constructor of adapter 
	 * @param context Context to retrieve 
	 */
	public PlatformAdapter(Context context) {
		this.context = context;
	}

	/**
	 * Set up callback listener
	 * @param listener
	 */
	public final void setListener(SocialActionListener listener){
		this.listener = listener;
	}
	
	/**
	 * Check if certain context has authorize info
	 * @return true if the authorizing info is valid
	 */
	public abstract boolean isAuthorized();
	
	/**
	 * Request authorization, will callback to {@link SocialActionListener} if set
	 * @param activity Activity running in, used to show dialog, etc.
	 */
	public abstract void authorize(Activity activity);
	
	/**
	 * Request user info, note that {@link #authorize(Activity)} should be completed first
	 */
	public abstract void getUserInfo();
	
	/**
	 * Request share, note that {@link #authorize(Activity)} should be completed first
	 * @param activity Activity running in, used to show dialog, etc. 
	 * @param values
	 */
	public abstract void share(Activity activity, Bundle values);
	
	/**
	 * Request logout, will cleanup corresponding access_token and user data, etc.
	 */
	public abstract void logout();
	
	/**
	 * Should be called in {@link Activity#onActivityResult(int, int, Intent)} when using SSO authorization method
	 * @param values Bundle value containing SSO authorization callback data
	 */
	public abstract void onActivityResult(Bundle values);
	
}
