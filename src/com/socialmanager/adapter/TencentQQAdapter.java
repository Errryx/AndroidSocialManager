package com.socialmanager.adapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;

import com.socialmanager.BuildConfig;
import com.socialmanager.Constant;
import com.socialmanager.PlatformAdapter;
import com.socialmanager.SocialActionListener;
import com.socialmanager.SocialManager.Action;
import com.socialmanager.SocialManager.Platform;
import com.socialmanager.util.TencentQQInfoKeeper;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class TencentQQAdapter extends PlatformAdapter implements IUiListener {

	private static final String SCOPE = "get_user_info,get_simple_userinfo,get_user_profile";
	
	private static final String ACCESS_CODE = "access_token";
	private static final String EXPIRES_IN = "expires_in";
	private static final String OPEN_ID = "open_id";
	
	private static final String NICKNAME = "nickname";
	private static final String FIGUREURL_QQ_1 = "figureurl_qq_1";
	private static final String FIGUREURL_QQ_2 = "figureurl_qq_2";
	
	private SocialActionListener listener = null;
	private Tencent tencent = null;

	public TencentQQAdapter(Context context) {
		super(context);

		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			tencent = Tencent.createInstance(appInfo.metaData.get(Constant.META_DATA_KEY_QQ_APPID).toString(), context.getApplicationContext());

			JSONObject json = TencentQQInfoKeeper.readAccessToken(context);
			if (json != null) {
				tencent.setAccessToken(json.getString(ACCESS_CODE), json.getString(EXPIRES_IN));
				tencent.setOpenId(json.getString(OPEN_ID));
			}
		} catch (NameNotFoundException e) {
			throw new IllegalArgumentException("Make sure \"qq_appid\" is INCLUDED in AndroidManifest.xml");
		} catch (JSONException e) {
			
		}
	}

	@Override
	public void getUserInfo() {
		tencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO, null, Constants.HTTP_GET, new RequestListener(isAuthorized()), null);
	}

	@Override
	public boolean isAuthorized() {
		return tencent.isSessionValid();
	}

	@Override
	public void authorize(Activity activity) {
		if (BuildConfig.DEBUG)
			Log.e(getClass().getName(), "TECENT LOGIN NOTICE: Using login SCOPE: " + SCOPE + " make sure this containing the permissions you need");
		tencent.login(activity, SCOPE, this);
	}

	@Override
	public void share(Activity activity, Bundle bundle) {

	}

	@Override
	public void logout() {
		tencent.logout(context);
		TencentQQInfoKeeper.clear(context);
		if (listener != null)
			listener.onActionCompleted(Platform.PLATFORM_QQ, Action.ACTION_LOGOUT, null);
	}

	@Override
	public void onActivityResult(Bundle values) {
		if (tencent != null) {
			int requestCode = values.getInt(Constant.BUNDLE_REQUEST_CODE);
			int resultCode = values.getInt(Constant.BUNDLE_RESULT_CODE);
			Intent data = values.getParcelable(Constant.BUNDLE_DATA);
			tencent.onActivityResult(requestCode, resultCode, data);
		} else
			throw new IllegalStateException("Tencent object is not initialized");
	}

	@Override
	public void onCancel() {
		if (listener != null)
			listener.onActionCanceled(Platform.PLATFORM_QQ, Action.ACTION_AUTORIZE);
	}

	@Override
	public void onComplete(JSONObject jsonObject) {
		TencentQQInfoKeeper.keepAccessToken(context, jsonObject);
		if (listener != null)
			listener.onActionCompleted(Platform.PLATFORM_QQ, Action.ACTION_AUTORIZE, null);
	}

	@Override
	public void onError(UiError error) {
		if (listener != null)
			listener.onActionFailed(Platform.PLATFORM_QQ, Action.ACTION_AUTORIZE, new Exception("Tecent UiError: "
							+ "\n errorCode:" + error.errorCode
							+ "\n errorMsg: " + error.errorMessage
							+ "\n errorDetail: " + error.errorDetail));
	}

	private class RequestListener implements IRequestListener {

		private static final String RET2 = "ret";
		private boolean needReAuth = false;

		public RequestListener(boolean needReAuth) {
			this.needReAuth = needReAuth;
		}

		@Override
		public void onComplete(JSONObject response, Object state) {
			try {
				doComplete(response, state);
				if (listener != null) {
					Bundle b = new Bundle();
					b.putString(Constant.BUNDLE_USR_ID, tencent.getOpenId());
					b.putString(Constant.BUNDLE_USR_NAME, response.getString(NICKNAME));
					b.putString(Constant.BUNDLE_USR_IMG_LARGE, response.getString(FIGUREURL_QQ_2));
					b.putString(Constant.BUNDLE_USR_IMG_SMALL, response.getString(FIGUREURL_QQ_1));
					listener.onActionCompleted(Platform.PLATFORM_QQ, Action.ACTION_SHOW_USER, b);
				}
			} catch (JSONException e) {
				Log.d(getClass().getName(), e.getLocalizedMessage());
				if (listener != null)
					listener.onActionFailed(Platform.PLATFORM_QQ, Action.ACTION_SHOW_USER, e);
			}
		}

		@Override
		public void onConnectTimeoutException(ConnectTimeoutException e, Object arg1) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_QQ, Action.ACTION_SHOW_USER, e);
		}

		@Override
		public void onHttpStatusException(HttpStatusException e, Object arg1) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_QQ, Action.ACTION_SHOW_USER, e);
		}

		@Override
		public void onIOException(IOException e, Object arg1) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_QQ, Action.ACTION_SHOW_USER, e);
		}

		@Override
		public void onJSONException(JSONException e, Object arg1) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_QQ, Action.ACTION_SHOW_USER, e);
		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object arg1) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_QQ, Action.ACTION_SHOW_USER, e);
		}

		@Override
		public void onNetworkUnavailableException(NetworkUnavailableException e, Object arg1) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_QQ, Action.ACTION_SHOW_USER, e);
		}

		@Override
		public void onSocketTimeoutException(SocketTimeoutException e, Object arg1) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_QQ, Action.ACTION_SHOW_USER, e);
		}

		@Override
		public void onUnknowException(Exception e, Object arg1) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_QQ, Action.ACTION_SHOW_USER, e);
		}

		protected void doComplete(JSONObject response, Object state) throws JSONException {
			if (response.getInt(RET2) == 100030) {
				if (needReAuth) {
					Runnable r = new Runnable() {
						public void run() {
							tencent.reAuth((Activity) context, SCOPE, TencentQQAdapter.this);
						}
					};
					((Activity) context).runOnUiThread(r);
				}
			}
		}
	}

}
