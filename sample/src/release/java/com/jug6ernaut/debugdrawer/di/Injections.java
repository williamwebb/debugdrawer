package com.jug6ernaut.debugdrawer.di;

import android.app.Activity;
import com.jug6ernaut.debugdrawer.DebugDrawer;
import com.jug6ernaut.debugdrawer.modules.*;
import io.jug6ernaut.debugdrawer.modules.TimberModule;

/**
 * Created by williamwebb on 4/2/16.
 */
public class Injections {

	public static DebugDrawer.Builder debugDrawer() {
		return new DebugDrawer.Builder();
	}

}
