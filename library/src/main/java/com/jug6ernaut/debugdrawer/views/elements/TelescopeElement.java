package com.jug6ernaut.debugdrawer.views.elements;

import android.app.Activity;
import com.jakewharton.u2020.ui.bugreport.BugReportLens;
import com.jug6ernaut.debugdrawer.R;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.ToggleElement;
import com.mattprecious.telescope.TelescopeLayout;

import static butterknife.ButterKnife.findById;

/**
 * Created by williamwebb on 7/4/15.
 */
public class TelescopeElement extends ToggleElement {

	TelescopeLayout telescopeLayout;

	public TelescopeElement() {
		super("Telescope", true, true);
	}

	@Override
	protected void onModuleAttached(Activity activity, DebugModule module) {
		super.onModuleAttached(activity,module);
		telescopeLayout = findById(activity, R.id.telescope_container);
		telescopeLayout.setLens(new BugReportLens(activity));

		TelescopeLayout.cleanUp(activity); // Clean up any old screenshots.

		// enable/disable based on saved preference
		onSwitch(isChecked());
	}

	@Override
	public void onSwitch(boolean state) {
		if(state) {
			telescopeLayout.setPointerCount(2);
		} else {
			// Effectively disabled
			telescopeLayout.setPointerCount(0);
		}
	}
}
