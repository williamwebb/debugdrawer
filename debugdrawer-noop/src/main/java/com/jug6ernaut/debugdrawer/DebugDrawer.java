package com.jug6ernaut.debugdrawer;

import android.app.Activity;
import com.jug6ernaut.debugdrawer.views.DebugElement;
import com.jug6ernaut.debugdrawer.views.DebugModule;

@SuppressWarnings("unused")
public final class DebugDrawer {
	private DebugDrawer() { }
	public static class Builder {
		public Builder() { }
		public Builder modules(DebugModule... modules) {
			return this;
		}
		public Builder elements(String groupName, DebugElement... elements) {
			return this;
		}
		public DebugDrawer bind(Activity activity) {
			return new DebugDrawer();
		}
	}
}
