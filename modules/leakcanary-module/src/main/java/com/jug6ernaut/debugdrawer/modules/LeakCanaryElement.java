package com.jug6ernaut.debugdrawer.modules;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.ToggleElement;
import com.squareup.leakcanary.LeakCanary;

import java.lang.ref.WeakReference;

/**
 * Created by williamwebb on 7/5/15.
 */
public class LeakCanaryElement extends ToggleElement {

	WeakReference<Activity> activityReference;

	public LeakCanaryElement() {
		super("Leak Canary");
	}

	@Override
	public void onSwitch(boolean state) {
		if(activityReference.get() != null) {
			Activity activity = activityReference.get();
			if (state) {
				LeakCanary.install(activity.getApplication());
			} else {
				Snackbar.make(activity.findViewById(android.R.id.content),
					"Restart Required to take effect",
					Snackbar.LENGTH_LONG)
				.setActionTextColor(Color.RED)
				.show();
			}
		}
	}

	@Override
	protected void onModuleAttached(Activity activity, DebugModule module) {
		activityReference = new WeakReference<>(activity);
		if(isChecked()) onSwitch(true);
	}

}
