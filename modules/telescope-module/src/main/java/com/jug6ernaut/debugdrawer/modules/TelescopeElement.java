package com.jug6ernaut.debugdrawer.modules;

import android.app.Activity;
import android.view.ViewGroup;
import com.jakewharton.u2020.ui.bugreport.BugReportLens;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.ToggleElement;
import com.mattprecious.telescope.TelescopeLayout;
import io.jug6ernaut.debugdrawer.modules.data.LumberYard;

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

		ViewGroup content = module.getContent();

		telescopeLayout = new TelescopeLayout(activity);

		ViewGroup parent = (ViewGroup) content.getParent();
		parent.removeView(content);
		parent.addView(telescopeLayout,0);
		telescopeLayout.addView(content);

		telescopeLayout.setLens(new BugReportLens(activity, LumberYard.getInstance(activity)));

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
			telescopeLayout.setPointerCount(Integer.MAX_VALUE);
		}
	}
}
