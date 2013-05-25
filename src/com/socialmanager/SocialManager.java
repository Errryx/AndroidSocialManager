package com.socialmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.socialmanager.adapter.RenrenAdapter;
import com.socialmanager.adapter.TencentQQAdapter;
import com.socialmanager.adapter.WeiboAdapter;

/**
 * Singleton class providing basic APIs to perform common action (check auth status, request auth, user info, share)
 *
 * @author eryx.kao@gmail.com
 *
 */
public class SocialManager {
	
	public static enum Platform {
		PLATFORM_WEIBO,
		PLATFORM_QQ,
		PLATFORM_RENREN;
	}
	
	public static enum Action {
		ACTION_AUTORIZE,
		ACTION_SHOW_USER,
		ACTION_SHARE,
		ACTION_LOGOUT,
		ACTION_ACTIVITY_RESULT;
	}
	
	private static SocialManager instance = null;
	private HashMap<Platform, PlatformAdapter> adapterMap = null;
	
	private SocialManager() {
		adapterMap = new HashMap<Platform, PlatformAdapter>();
	}
	
	/**
	 * Retrieve a instance of {@link SocialManager}
	 * @return instance of {@link SocialManager}
	 */
	public static SocialManager getInstance() {
		if (instance == null)
			instance = new SocialManager();
		return instance;
	}
	
	/**
	 * Cleanup private fields
	 */
	public static void destroy() {
		if (instance != null) {
			instance.adapterMap.clear();
            instance.adapterMap = null;
        }
		instance = null;
	}
	
	/**
	 * Get a list containing the platforms that has the authorization info for this context
	 * @param context Context to check
	 * @return List containing the described platform
	 */
	public List<Platform> getAuthorizedPlatforms(Context context) {
		List<Platform> list = null;
		for (Platform platform : Platform.values()) {
			PlatformAdapter adapter = getAdapter(context, platform, null);
			if (adapter == null) continue;
			else if (adapter.isAuthorized()) {
				if (list == null) list = new ArrayList<Platform>();
				list.add(platform);
			}
		}
		return list;
	}
	
	/**
	 * Perform certain action on specified platform asynchronously
	 * @param activity Activity running in
	 * @param platform Platform to take the action
	 * @param action Action to perform
	 * @param values Bundle values to submit
	 * @param listener Listener to callback
	 */
	public void performAction(Activity activity, Platform platform, Action action, Bundle values, SocialActionListener listener) {
		PlatformAdapter adapter = getAdapter(activity, platform, listener);
		switch (action) {
		case ACTION_AUTORIZE:
			adapter.authorize(activity);
			break;
		case ACTION_SHOW_USER:
			adapter.getUserInfo();
			break;
		case ACTION_SHARE:
			adapter.share(activity, values);
			break;
		case ACTION_LOGOUT:
			adapter.logout();
			break;
		case ACTION_ACTIVITY_RESULT:
			adapter.onActivityResult(values);
			break;
		default:
			break;
		}
	}

	private PlatformAdapter getAdapter(Context context, Platform platform, SocialActionListener listener) {
		PlatformAdapter adapter = adapterMap.get(platform);
		if (adapter != null) {
			adapter.setListener(listener);
			return adapter;
		}

		switch(platform) {
		case PLATFORM_QQ:
			adapter = new TencentQQAdapter(context);
			break;
		case PLATFORM_RENREN:
			adapter = new RenrenAdapter(context);
			break;
		case PLATFORM_WEIBO:
			adapter = new WeiboAdapter(context);
			break;
		default:
			break;
		}

		if (adapter != null) {
			adapter.setListener(listener);
			adapterMap.put(platform, adapter);
		}
		return adapter;
	}
	
}
