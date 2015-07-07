package com.jug6ernaut.debugdrawer.views.elements;

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
		if(state) {
			LeakCanary.install(activityReference.get().getApplication());
		} else {
			Snackbar.make(activityReference.get().findViewById(android.R.id.content),
					"Restart Required to take effect", Snackbar.LENGTH_LONG)
					.setActionTextColor(Color.RED)
					.show();
		}
	}

	@Override
	protected void onModuleAttached(Activity activity, DebugModule module) {
		activityReference = new WeakReference<>(activity);
		if(isChecked()) onSwitch(true);
	}

}
