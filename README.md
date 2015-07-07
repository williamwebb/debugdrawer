debugdrawer
===========

HEAVILY inspired/built off of JakeWharton's awesome work in [u2020](https://github.com/JakeWharton/u2020).

	    new DebugDrawer()
            .elements("Network",new NetworkWatcher(this))
		    .elements("UI", 
                new TelescopeElement(), 
                new AnimationSpeedElement(), 
                new LeakCanaryElement())
		    .modules(
                new BuildModule(), 
                new DeviceInfoModule(), 
                new MadgeModule(), 
                new ScalpelModule(), 
                new GhostModule())
	    .bind(this);

![](vid.gif)
