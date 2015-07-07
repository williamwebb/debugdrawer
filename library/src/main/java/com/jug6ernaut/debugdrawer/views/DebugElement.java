package com.jug6ernaut.debugdrawer.views;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by williamwebb on 6/28/14.
 */
public abstract class DebugElement {

	public abstract View onCreateView(DebugModule parent, LayoutInflater inflater, ViewGroup root);
	protected void onViewCreated(View view){ }
	protected void onModuleAttached(Activity activity, DebugModule module){ }
	protected void onDrawerOpened() {}
	protected void onDrawerClosed() {}
	protected void onActivityStart() {}
	protected void onActivityStop() {}

	private boolean isEnabled  = true;
	private View view;
	private DebugModule parent;

	final View create(DebugModule parent, LayoutInflater inflater, ViewGroup root) {
		this.parent = parent;
		this.view = onCreateView(parent, inflater, root);
		view.setEnabled(isEnabled());
		onViewCreated(view);

		return view;
	}

	public DebugModule getParent() {
		return parent;
	}

	public void setEnabled(boolean enable) {
		if(view != null) view.setEnabled(enable);
		this.isEnabled = enable;
	}

	public Boolean isEnabled() {
		return isEnabled;
	}

	public boolean attached() {
		return parent != null;
	}

}
