package com.jug6ernaut.debugdrawer.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.jug6ernaut.debugdrawer.DebugDrawer;
import com.jug6ernaut.debugdrawer.views.SpinnerElement;
import com.jug6ernaut.debugdrawer.views.WatcherElement;
import com.jug6ernaut.debugdrawer.views.elements.AnimationSpeedElement;
import com.jug6ernaut.debugdrawer.views.elements.LeakCanaryElement;
import com.jug6ernaut.debugdrawer.views.elements.RiseAndShineElement;
import com.jug6ernaut.debugdrawer.views.elements.TelescopeElement;
import com.jug6ernaut.debugdrawer.views.modules.*;

public class DemoActivity extends AppCompatActivity {

    private static String TAG = "demo";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);

	    new DebugDrawer()
            .elements("Network",new NetworkWatcher(this))
		    .elements("UI",
                new TelescopeElement(),
                new AnimationSpeedElement(),
                new LeakCanaryElement(),
                new RiseAndShineElement(),
                new aElement())
		    .modules(
                new BuildModule(),
                new DeviceInfoModule(),
                new MadgeModule(),
                new ScalpelModule(),
                new GhostModule())
	    .bind(this);
    }

    public void onClick1(View v){
        Log.v(TAG,"verbose");
        Log.i(TAG, "info");
        Log.d(TAG, "debug");
        Log.w(TAG, "warning");
        Log.e(TAG, "error");
        Log.wtf(TAG, "wtf");
    }

    public void onClick2(View v){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int x=0;x<5;x++){
                    Log.i(TAG,x+"");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        }).start();

    }

    private class aElement extends SpinnerElement {

        public aElement() {
            super("", new String[]{"1","2"});
        }
        @Override
        public void onItemSelect(String item) {

        }
    }

    // Do not actually use this :) leaky leaky leaky
    private class NetworkWatcher extends WatcherElement {
        public NetworkWatcher(Context context) {
            super("Connection","");

            context.registerReceiver(new BroadcastReceiver() {
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
    };

}
