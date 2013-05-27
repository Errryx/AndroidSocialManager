package com.socialmanager.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.renren.api.connect.android.AsyncRenren;
import com.renren.api.connect.android.Renren;
import com.renren.api.connect.android.common.AbstractRequestListener;
import com.renren.api.connect.android.exception.RenrenAuthError;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.users.UsersGetInfoRequestParam;
import com.renren.api.connect.android.users.UsersGetInfoResponseBean;
import com.renren.api.connect.android.view.RenrenAuthListener;
import com.socialmanager.Constant;
import com.socialmanager.PlatformAdapter;
import com.socialmanager.SocialActionListener;
import com.socialmanager.SocialManager.Action;
import com.socialmanager.SocialManager.Platform;
import com.socialmanager.util.RenrenInfoKeeper;

public class RenrenAdapter extends PlatformAdapter implements RenrenAuthListener {

	private Renren renren = null;
	
	public RenrenAdapter(Context context) {
		super(context);
		try {
			ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			String api_key = appInfo.metaData.get(Constant.META_DATA_KEY_RENREN_API_KEY).toString();
			String app_id = appInfo.metaData.get(Constant.META_DATA_KEY_RENREN_APP_ID).toString();
			String secret_key = appInfo.metaData.get(Constant.META_DATA_KEY_RENREN_SECRET_KEY).toString();
			renren = new Renren(api_key, secret_key, app_id, context);
			renren.init(context);
		} catch (NameNotFoundException e) {
		}
	}
	
	@Override
	public void getUserInfo() {
		AsyncRenren asyncRenren = new AsyncRenren(renren);
		UsersGetInfoRequestParam param = new UsersGetInfoRequestParam(new String[]{ String.valueOf(renren.getCurrentUid())});
		asyncRenren.getUsersInfo(param, userGetInfoListener);
	}

	@Override
	public boolean isAuthorized() {
		return renren.isAccessTokenValid();
	}

	@Override
	public void authorize(Activity activity) {
		renren.authorize(activity, this);
	}

	@Override
	public void share(Activity activity, Bundle bundle) {
		
	}

	@Override
	public void logout() {
		renren.logout(context);
		if (listener != null)
			listener.onActionCompleted(Platform.PLATFORM_RENREN, Action.ACTION_LOGOUT, null);
	}

	@Override
	public void onActivityResult(Bundle values) {
		int requestCode = values.getInt(Constant.BUNDLE_REQUEST_CODE);
		int resultCode = values.getInt(Constant.BUNDLE_RESULT_CODE);
		Intent data = values.getParcelable(Constant.BUNDLE_DATA);
		if (renren != null)
			renren.authorizeCallback(requestCode, resultCode, data);
	}

	@Override
	public void onComplete(Bundle values) {
		RenrenInfoKeeper.keepAccessToken(context, values);
		if (listener != null)
			listener.onActionCompleted(Platform.PLATFORM_RENREN, Action.ACTION_AUTORIZE, null);
	}

	@Override
	public void onRenrenAuthError(RenrenAuthError renrenAuthError) {
		if (listener != null)
			listener.onActionFailed(Platform.PLATFORM_RENREN, Action.ACTION_AUTORIZE, renrenAuthError);
	}

	@Override
	public void onCancelLogin() {
		if (listener != null)
			listener.onActionCanceled(Platform.PLATFORM_RENREN, Action.ACTION_AUTORIZE);
	}

	@Override
	public void onCancelAuth(Bundle values) {
		if (listener != null)
			listener.onActionCanceled(Platform.PLATFORM_RENREN, Action.ACTION_AUTORIZE);
	}
	
	AbstractRequestListener<UsersGetInfoResponseBean> userGetInfoListener = new AbstractRequestListener<UsersGetInfoResponseBean>() {

		@Override
		public void onComplete(final UsersGetInfoResponseBean bean) {
			Bundle bundle = new Bundle();
			bundle.putString(Constant.BUNDLE_USR_ID, String.valueOf(bean.getUsersInfo().get(0).getUid()));
			bundle.putString(Constant.BUNDLE_USR_NAME, bean.getUsersInfo().get(0).getName());
			bundle.putString(Constant.BUNDLE_USR_IMG_SMALL, bean.getUsersInfo().get(0).getHeadurl());
			bundle.putString(Constant.BUNDLE_USR_IMG_LARGE, bean.getUsersInfo().get(0).getTinyurl());
			if (listener != null)
				listener.onActionCompleted(Platform.PLATFORM_RENREN, Action.ACTION_SHOW_USER, bundle);
		}

		@Override
		public void onRenrenError(final RenrenError renrenError) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_RENREN, Action.ACTION_SHOW_USER, renrenError);
		}

		@Override
		public void onFault(final Throwable fault) {
			if (listener != null)
				listener.onActionFailed(Platform.PLATFORM_RENREN, Action.ACTION_SHOW_USER, new Exception(fault));
		}
	};
}
