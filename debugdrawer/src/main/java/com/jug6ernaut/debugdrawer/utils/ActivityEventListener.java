package com.jug6ernaut.debugdrawer.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;

/**
 * Created by williamwebb on 7/3/15.
 */
@SuppressWarnings("unused")
public abstract class ActivityEventListener {

	private WeakReference<Activity> activity;

	public ActivityEventListener(Activity activity) {
		this.activity = new WeakReference<>(activity);
	}

	public void register(Application application) {
		application.registerActivityLifecycleCallbacks(callbacks);
	}

	public void unregister(Application application) {
		application.unregisterActivityLifecycleCallbacks(callbacks);
	}

	// don't expose
	private Application.ActivityLifecycleCallbacks callbacks = new Application.ActivityLifecycleCallbacks() {
		@Override
		public void onActivityCreated(Activity activity, Bundle bundle) {
			if (isThisActivity(activity)) onCreate(activity, bundle);
		}
		@Override
		public void onActivityStarted(Activity activity) {
			if (isThisActivity(activity)) onStart(activity);
		}
		@Override
		public void onActivityResumed(Activity activity) {
			if (isThisActivity(activity)) onResumed(activity);
		}
		@Override
		public void onActivityPaused(Activity activity) {
			if (isThisActivity(activity)) onPaused(activity);
		}
		@Override
		public void onActivityStopped(Activity activity) {
			if (isThisActivity(activity)) onStopped(activity);
		}
		@Override
		public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
			if (isThisActivity(activity)) onSaveInstanceState(activity, bundle);
		}
		@Override
		public void onActivityDestroyed(Activity activity) {
			if (isThisActivity(activity)) {
				onDestroyed(activity);
				activity.getApplication().unregisterActivityLifecycleCallbacks(this);
				ActivityEventListener.this.activity.clear();
			}
		}
	};

	private boolean isThisActivity(Activity activity) {
		return (
				this.activity.get() != null &&
				this.activity.get().equals(activity)
		);
	}

	public void onCreate(Activity activity, Bundle bundle) { }
	public void onStart(Activity activity) { }
	public void onResumed(Activity activity) { }
	public void onPaused(Activity activity) { }
	public void onStopped(Activity activity) { }
	public void onSaveInstanceState(Activity activity, Bundle bundle) { }
	public void onDestroyed(Activity activity) { }
}
