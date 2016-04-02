package com.jug6ernaut.debugdrawer.di;

import com.jug6ernaut.debugdrawer.DebugDrawer;
import com.jug6ernaut.debugdrawer.modules.*;
import io.jug6ernaut.debugdrawer.modules.TimberModule;

/**
 * Created by williamwebb on 4/2/16.
 */
public class Injections {

	public static DebugDrawer.Builder debugDrawer() {
		return new DebugDrawer.Builder()
				.elements("UI",
						new TelescopeElement(),
						new AnimationSpeedElement(),
						new LeakCanaryElement(),
						new RiseAndShineElement())
				.elements("Network", new NetworkWatcher())
				.modules(
						new BuildModule(),
						new DeviceInfoModule(),
						new MadgeModule(),
						new ScalpelModule())
				.elements("Logs",new TimberModule());
	}

}
