package com.jug6ernaut.debugdrawer.di;

import android.app.Activity;
import com.jug6ernaut.debugdrawer.DebugDrawer;
import com.jug6ernaut.debugdrawer.modules.*;
import dagger.Module;
import dagger.Provides;
import io.jug6ernaut.debugdrawer.modules.TimberModule;

@Module
public class DebugDrawerModule {
	private final Activity activity;

	public DebugDrawerModule(Activity activity) {
		this.activity = activity;
	}

	@Provides
	public DebugDrawer.Builder debugDrawer() {
		return new DebugDrawer.Builder()
			.elements("UI",
					new TelescopeElement(),
					new AnimationSpeedElement(),
					new LeakCanaryElement(),
					new RiseAndShineElement())
			.elements("Network", new NetworkWatcher(activity))
			.modules(
					new BuildModule(),
					new DeviceInfoModule(),
					new MadgeModule(),
					new ScalpelModule())
			.elements("Logs",new TimberModule());
	}
}