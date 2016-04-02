package com.jug6ernaut.debugdrawer.di;

import com.jug6ernaut.debugdrawer.DebugDrawer;

/**
 * Created by williamwebb on 4/2/16.
 */
public class Injections {

	public static DebugDrawer.Builder debugDrawer() {
		return new DebugDrawer.Builder();
	}

}
