package com.jug6ernaut.debugdrawer.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.jug6ernaut.debugdrawer.di.Injections;
import timber.log.Timber;

public class DemoActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		    this.setContentView(R.layout.main);

        Timber.plant(new Timber.DebugTree());

        Injections
            .debugDrawer()
            .bind(this);
    }

    public void onClick1(View v){
        Timber.d("verbose");
        Timber.i("info");
        Timber.d("debug");
        Timber.w("warning");
        Timber.e("error");
        Timber.wtf("wtf");
    }

    public void onClick2(View v){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int x=0;x<5;x++){
                    Timber.i(x+"");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                }
            }
        }).start();

    }
}
