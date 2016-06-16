DebugDrawer [![Build Status](https://travis-ci.org/williamwebb/debugdrawer.svg?branch=master)](https://travis-ci.org/williamwebb/debugdrawer)
===========

HEAVILY inspired/built off of JakeWharton's awesome work in [u2020](https://github.com/JakeWharton/u2020) DebugDrawer is small but highly extendable library built to allow developers to easily add a DebugDrawer to their Android applications.

Overview
========
In your applications you often need to change certain configuration settings, monitor internal state or simply try to understand what, DebugDrawer allows you to easily add a slide out drawer with the ability to do this. Classes are provided to allow you to easily create your own additions to customize to your needs.

![](vid.gif)
	    
Usage
=====
In your `Activity` class:

	new DebugDrawer.Builder()
		.elements("UI",
			new TelescopeElement(),
			new AnimationSpeedElement(),
			new LeakCanaryElement(),
			new RiseAndShineElement())
		.elements("Network", new NetworkWatcher(this))
		.modules(
			new BuildModule(),
			new DeviceInfoModule(),
			new MadgeModule(),
			new ScalpelModule())
		.elements("Logs",new TimberModule())
		.bind(this);
			
In your `build.gradle`:

	debugCompile 'com.jug6ernaut.debugdrawer:debugdrawer:{latest}'
	releaseCompile 'com.jug6ernaut.debugdrawer:debugdrawer-noop:{latest}'
	testCompile 'com.jug6ernaut.debugdrawer:debugdrawer-noop:{latest}'
	...
	debugCompile 'com.jug6ernaut.debugdrawer:module-deviceinfo:0.7.0'
	...

Download
--------

Gradle:

```groovy
debugCompile 'com.jug6ernaut.debugdrawer:debugdrawer:0.7.0'
```

License
-------

    Copyright 2016 William Webb

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    
