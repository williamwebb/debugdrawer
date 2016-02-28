package com.jug6ernaut.debugdrawer.di;

import android.app.Activity;
import com.jug6ernaut.debugdrawer.DebugDrawer;
import dagger.Module;
import dagger.Provides;

@Module
public class DebugDrawerModule {

	public DebugDrawerModule(Activity activity) { }

	@Provides
	public DebugDrawer.Builder debugDrawer() {
		return new DebugDrawer.Builder();
	}
}