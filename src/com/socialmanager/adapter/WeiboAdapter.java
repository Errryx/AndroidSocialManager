package com.socialmanager.adapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialmanager.Constant;
import com.socialmanager.PlatformAdapter;
import com.socialmanager.SocialActionListener;
import com.socialmanager.SocialManager.Action;
import com.socialmanager.SocialManager.Platform;
import com.socialmanager.model.AccessToken;
import com.socialmanager.model.ActionShowUser;
import com.socialmanager.util.AccessTokenAPI;
import com.socialmanager.util.WeiboInfoKeeper;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.AccountAPI;
import com.weibo.sdk.android.api.UsersAPI;
import com.weibo.sdk.android.net.RequestListener;
import com.weibo.sdk.android.sso.SsoHandler;

public class WeiboAdapter extends PlatformAdapter implements WeiboAuthListener {

	private SocialActionListener listener = null;
	private Weibo weibo = null;
	private SsoHandler ssoHandler = null;
	
	public static Oauth2AccessToken accessToken = null;

	public WeiboAdapter(Context context) {
		super(context);
		
		try {
			ApplicationInfo  appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			String app_key = appInfo.metaData.get(Constant.META_DATA_KEY_WEIBO_APPKEY).toString();
			String redirect_url = appInfo.metaData.get(Constant.META_DATA_KEY_WEIBO_REDIRECT).toString();
			weibo = Weibo.getInstance(app_key, redirect_url, "all");
		} catch (NameNotFoundException e) {
			Log.d(getClass().getName(), e.getLocalizedMessage());
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("Make sure app key, secret key and redirect_url of weibo are INCLUDED in AndroidManifest.xml");
		}
		
		WeiboAdapter.accessToken = WeiboInfoKeeper.readAccessToken(context);
	}

	@Override
	public void getUserInfo() {
		UsersAPI api = new UsersAPI(WeiboAdapter.accessToken);
		api.show(Long.parseLong(WeiboInfoKeeper.readUid(context)), uidRequestListener);
	}

	@Override
	public boolean isAuthorized() {
		return WeiboAdapter.accessToken.isSessionValid();
	}

	@Override
	public void authorize(Activity activity) {
		ssoHandler = new SsoHandler((Activity) context, weibo);
		ssoHandler.authorize(this);
	}

	@Override
	public void share(Activity activity, Bundle values) {
		
	}
	
	@Override
	public void logout() {
		AccountAPI api = new AccountAPI(accessToken);
		api.endSession(endSessionRequestListener);
		WeiboInfoKeeper.clear(context);
	}

	@Override
	public void onActivityResult(Bundle values) {
		int requestCode = values.getInt(Constant.BUNDLE_REQUEST_CODE);
		int resultCode = values.getInt(Constant.BUNDLE_RESULT_CODE);
		Intent data = values.getParcelable(Constant.BUNDLE_DATA);
		if (ssoHandler != null)
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	}

	@Override
	public void onCancel() {
		if (listener != null)
			listener.onActionCanceled(Platform.PLATFORM_WEIBO, Action.ACTION_AUTORIZE);
	}

	@Override
	public void onComplete(Bundle values) {
		if (!values.containsKey("code")) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_AUTORIZE, 
						new IllegalArgumentException("Missing key \"code\" in returning value"));
			return;
		}
		
		AccessTokenAPI api = new AccessTokenAPI();
		api.accessToken(context, values.getString("code"), accessTokenRequestListener);
	}

	@Override
	public void onError(WeiboDialogError e) {
		Log.d(getClass().getName(), e.getLocalizedMessage());
		if (listener != null)
			listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_AUTORIZE, new Exception(e.getCause()));
	}

	@Override
	public void onWeiboException(WeiboException e) {
		Log.d(getClass().getName(), e.getLocalizedMessage());
		if (listener != null)
			listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_AUTORIZE, e);
	}
	
	private RequestListener uidRequestListener = new RequestListener() {
		
		@Override
		public void onIOException(IOException e) {
			Log.d(getClass().getName(), e.getLocalizedMessage());
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_SHOW_USER, e);
		}
		
		@Override
		public void onError(WeiboException e) {
			Log.d(getClass().getName(), e.getLocalizedMessage());
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_SHOW_USER, e);
		}
		
		@Override
		public void onComplete4binary(ByteArrayOutputStream stream) {
			Log.e(getClass().getName(), "Method onComplete4binary(ByteArrayOutputStream) is not implemented");
		}
		
		@Override
		public void onComplete(String result) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				ActionShowUser actionShowUser = mapper.readValue(result, ActionShowUser.class);
				Bundle b = new Bundle();
				b.putLong(Constant.BUNDLE_USR_ID, actionShowUser.getId());
				b.putString(Constant.BUNDLE_USR_NAME, actionShowUser.getScreen_name());
				b.putString(Constant.BUNDLE_USR_IMG_LARGE, actionShowUser.getAvatar_large());
				b.putString(Constant.BUNDLE_USR_IMG_SMALL, actionShowUser.getProfile_image_url());
				if (listener != null)
					listener.onActionCompleted(Platform.PLATFORM_WEIBO, Action.ACTION_SHOW_USER, b);
			} catch (Exception e) {
				Log.d(getClass().getName(), e.getLocalizedMessage());
				if (listener != null)
					listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_SHOW_USER, e);
			}
		}
	};
	
	private RequestListener accessTokenRequestListener = new RequestListener() {
		
		@Override
		public void onIOException(IOException e) {
			Log.d(getClass().getName(), e.getLocalizedMessage());
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_AUTORIZE, e);
		}
		
		@Override
		public void onError(WeiboException e) {
			Log.d(getClass().getName(), e.getLocalizedMessage());
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_AUTORIZE, e);
		}
		
		@Override
		public void onComplete4binary(ByteArrayOutputStream stream) {
			Log.e(getClass().getName(), "Method onComplete4binary(ByteArrayOutputStream) is not implemented");
		}
		
		@Override
		public void onComplete(String result) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				AccessToken token = mapper.readValue(result, AccessToken.class);
				accessToken = new Oauth2AccessToken(token.getAccess_token(), token.getExpires_in());

				WeiboInfoKeeper.keepAccessToken(context, accessToken);
				WeiboInfoKeeper.keepUId(context, token.getUid());
				if (listener != null)
					listener.onActionCompleted(Platform.PLATFORM_WEIBO, Action.ACTION_AUTORIZE, null);
			} catch (Exception e) {
				Log.d(getClass().getName(), e.getLocalizedMessage());
				if (listener != null)
					listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_AUTORIZE, e);
			}
		}
	};
	
	private RequestListener endSessionRequestListener = new RequestListener() {
		
		@Override
		public void onIOException(IOException e) {
			Log.d(getClass().getName(), e.getLocalizedMessage());
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_LOGOUT, e);
		}
		
		@Override
		public void onError(WeiboException e) {
			Log.d(getClass().getName(), e.getLocalizedMessage());
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_WEIBO, Action.ACTION_LOGOUT, e);
		}
		
		@Override
		public void onComplete4binary(ByteArrayOutputStream arg0) {
			Log.e(getClass().getName(), "Method onComplete4binary(ByteArrayOutputStream) is not implemented");
		}
		
		@Override
		public void onComplete(String arg0) {
			WeiboInfoKeeper.clear(context);
			if (listener != null)
				listener.onActionCompleted(Platform.PLATFORM_WEIBO, Action.ACTION_LOGOUT, null);
		}
	};

}
