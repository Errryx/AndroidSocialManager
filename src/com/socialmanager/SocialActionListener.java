package com.socialmanager;

import android.os.Bundle;

import com.socialmanager.SocialManager.Action;
import com.socialmanager.SocialManager.Platform;

public interface SocialActionListener {

	/**
	 * Callback for completion, note that this method is called in sub-thread
	 * @param platform Platform for the callback
	 * @param action Action for the callback
	 * @param bundle Bundle value containing completion data, please refer to {@link Constant} for the keys
	 */
	public void onActionCompleted(Platform platform, Action action, Bundle bundle);
	
	/**
	 * Callback for failure, note that this method is called in sub-thread
	 * @param platform Platform for the callback
	 * @param action Action for the callback
	 * @param bundle Bundle value containing failure data, please refer to {@link Constants} for the keys
	 */
	public void onActionFailed(Platform platform, Action action, Exception e);
	
	/**
	 * Callback for canceling, note that this method is called in sub-thread
	 * @param platform Platform for this callback
	 * @param action Action for this callback
	 */
	public void onActionCanceled(Platform platform, Action action);
}
