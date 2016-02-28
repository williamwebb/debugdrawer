package com.jug6ernaut.debugdrawer.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import autodagger.AutoComponent;
import autodagger.AutoInjector;
import com.jug6ernaut.debugdrawer.DebugDrawer;
import com.jug6ernaut.debugdrawer.di.DebugDrawerModule;
import timber.log.Timber;

import javax.inject.Inject;

@AutoComponent(
    dependencies = DebugDrawerModule.class
) @AutoInjector
public class DemoActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);

        Timber.plant(new Timber.DebugTree());

        DaggerDemoActivityComponent.builder()
            .debugDrawerModule(new DebugDrawerModule(this))
            .build().inject(this);
    }

    // Using DI we can keep all DebugDrawer code only in debug builds.
    @Inject
    void injectDebugDrawer(DebugDrawer.Builder builder) {
        builder.bind(this);
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
                    } catch (InterruptedException e) {}
                }
            }
        }).start();

    }
}
