package com.socialmanager.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.socialmanager.Constant;
import com.weibo.sdk.android.WeiboParameters;
import com.weibo.sdk.android.api.WeiboAPI;
import com.weibo.sdk.android.net.AsyncWeiboRunner;
import com.weibo.sdk.android.net.RequestListener;

/**
 * Class to get access token by authorize code
 * @author miaogao
 *
 */
public class AccessTokenAPI {

    private static final String SERVER_URL_PRIX = "https://api.weibo.com/oauth2/access_token?";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_TYPE = "grant_type";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String CODE = "code";
    
	public AccessTokenAPI() {
	}
	
	/**
	 * get access_token by code returned by authorization, result will be given to listener
	 * @param code
	 * @param listener
	 */
	public void accessToken(Context context, String code,RequestListener listener) {

		ApplicationInfo appInfo;
		try {
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			String app_key = appInfo.metaData.get(Constant.META_DATA_KEY_WEIBO_APPKEY).toString();
			String secret = appInfo.metaData.get(Constant.META_DATA_KEY_WEIBO_SECRET).toString();
			String redirect_url = appInfo.metaData.get(Constant.META_DATA_KEY_WEIBO_REDIRECT).toString();
			
			WeiboParameters params = new WeiboParameters();
			params.add(CLIENT_ID, app_key);
			params.add(CLIENT_SECRET, secret);
			params.add(GRANT_TYPE, "authorization_code");
			params.add(REDIRECT_URI, redirect_url);
			params.add(CODE, code);
			
			AsyncWeiboRunner.request(SERVER_URL_PRIX, params, WeiboAPI.HTTPMETHOD_POST, listener);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

}
