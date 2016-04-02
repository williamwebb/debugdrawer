package com.jug6ernaut.debugdrawer.views;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.widget.TextView;
import com.jug6ernaut.debugdrawer.R;

/**
 * Created by williamwebb on 7/3/15.
 */
public class WatcherElement extends TextElement implements ExpandableView.OnStateChangeListener{

	private Handler handler  = new Handler();
	private boolean flashing = false;
	private ExpandableView parent;
	private int originalColor;
	private int flashColor;

	public WatcherElement(String name, String value) {
		super(name, value);
	}

	public void notifyOfEvent(String value) {
		setValue(value);

		if (attached() && !parent.isExpanded() && !flashing) {
			startFlash();
		}
	}

	@Override
	protected void onModuleAttached(Activity activity, DebugModule module) {
		parent = module.getView();
		originalColor = parent.titleView.getCurrentTextColor();
		flashColor = activity.getResources().getColor(R.color.accent2);
		parent.setOnStateChangeListener(this);
	}

	@Override
	public void onStateChanged(boolean expanded) {
		if(expanded && flashing) stopFlash();
	}

	private void startFlash() {
		flashing = true;
		handler.postDelayed(myRunner,1000);
	}

	private void stopFlash() {
		flashing = false;
		if(myRunner != null) handler.removeCallbacks(myRunner);
		if(parent != null && parent.titleView != null) parent.titleView.setTextColor(originalColor);
	}

	private Runnable myRunner = new Runnable() {
		@Override
		public void run() {
			TextView view = parent.titleView;
			int currentColor = view.getCurrentTextColor();
			if (currentColor == originalColor) {
				view.setTextColor(flashColor);
			} else {
				view.setTextColor(originalColor);
			}

			handler.postDelayed(this, 1000);
		}
	};

}
