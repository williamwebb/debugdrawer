package com.jug6ernaut.debugdrawer.sample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.jug6ernaut.debugdrawer.DebugDrawerHelper;
import com.jug6ernaut.debugdrawer.views.*;

public class DemoActivity extends Activity {

    private static String TAG = "demo";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DebugGroup testGroup = new DebugGroup("TestGroup",this);
        testGroup.addElement(new TextElement(this,"TextElement","Value"));
        testGroup.addElement(new ToggleElement("ToggleElement",this) {
            @Override
            public void onAction(Boolean aBoolean) {
                System.out.println("onAction: " + aBoolean);

                Toast.makeText(DemoActivity.this,"Toggle: " + aBoolean,Toast.LENGTH_SHORT).show();
            }
        });
        testGroup.addElement(new SpinnerElement(this,"SpinnerElement",R.array.levels_entries) {
            @Override
            public void onAction(String s) {
                System.out.println("onAction: " + s);
                Toast.makeText(DemoActivity.this,"Spinner: " + s,Toast.LENGTH_SHORT).show();
            }
        });
        testGroup.addElement(new DebugElement(this,"Custom") {
            @Override public void onAction(Object o) { /* nothing */ }

            @Override
            protected View createView() {
                ImageView imageView = new ImageView(DemoActivity.this);
                imageView.setImageResource(R.drawable.ic_launcher);
                return imageView;
            }
        });

        DebugDrawerHelper.addGroup(testGroup);
        DebugDrawerHelper.attach(this, R.layout.main);
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

}
