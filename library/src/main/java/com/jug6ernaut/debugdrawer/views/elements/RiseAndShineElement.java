package com.jug6ernaut.debugdrawer.views.elements;

import android.app.Activity;
import android.os.PowerManager;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.ToggleElement;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.*;
import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

/**
 * Created by williamwebb on 7/7/15.
 */
public class RiseAndShineElement extends ToggleElement {

	public RiseAndShineElement() {
		super("Rise & Shine");
	}

	@Override public void onSwitch(boolean state) { }

	@Override
	protected void onModuleAttached(Activity activity, DebugModule module) {
		super.onModuleAttached(activity,module);

		if(isChecked()) riseAndShine(activity);
	}


	/**
	 * Show the activity over the lock-screen and wake up the device. If you launched the app manually
	 * both of these conditions are already true. If you deployed from the IDE, however, this will
	 * save you from hundreds of power button presses and pattern swiping per day!
	 */
	private static void riseAndShine(Activity activity) {
		activity.getWindow().addFlags(FLAG_SHOW_WHEN_LOCKED);

		PowerManager power = (PowerManager) activity.getSystemService(POWER_SERVICE);
		PowerManager.WakeLock lock =
				power.newWakeLock(FULL_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE, "wakeup!");
		lock.acquire();
		lock.release();
	}
}
