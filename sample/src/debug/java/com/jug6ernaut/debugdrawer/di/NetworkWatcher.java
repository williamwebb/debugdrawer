package com.jug6ernaut.debugdrawer.di;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.WatcherElement;

/**
 * Created by williamwebb on 2/27/16.
 */
// Do not actually use this :) leaky leaky leaky
public class NetworkWatcher extends WatcherElement {
	public NetworkWatcher() {
		super("Connection","");
	}

	protected void onModuleAttached(Activity activity, DebugModule module) {
		activity.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getExtras() != null) {
					NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
					if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
						notifyOfEvent(ni.getTypeName());
					} else {
						notifyOfEvent("None");
					}
				}
				if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
					notifyOfEvent("No Connection");
				}
			}
		}, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

}