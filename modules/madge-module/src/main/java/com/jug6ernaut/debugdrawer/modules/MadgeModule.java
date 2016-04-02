package com.jug6ernaut.debugdrawer.modules;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import com.jakewharton.madge.MadgeFrameLayout;
import com.jug6ernaut.debugdrawer.DebugView;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.ToggleElement;
import com.jug6ernaut.saber.preferences.BooleanPreference;
import com.jug6ernaut.saber.preferences.Preference;
import timber.log.Timber;

/**
 * Created by williamwebb on 7/2/14.
 */
public class MadgeModule extends DebugModule {

    Preference<Boolean> pixelGridEnabled;
    Preference<Boolean> pixelRatioEnabled;
    MadgeFrameLayout  madgeFrameLayout;
    ToggleElement     uiPixelGridElement;
    ToggleElement     uiPixelRatioElement;

    public MadgeModule() {
        super("UI");
    }

    public MadgeModule(String moduleName) {
        super(moduleName);
    }

    @Override
    protected void onAttach(Activity activity, DebugView parent, ViewGroup content) {
        SharedPreferences sp = activity.getPreferences(Context.MODE_PRIVATE);
        pixelGridEnabled = new BooleanPreference(sp,"pixelGridEnabled");
        pixelRatioEnabled = new BooleanPreference(sp,"pixelRatioEnabled");

        uiPixelGridElement = new ToggleElement("Pixel Grid") {
            @Override
            public void onSwitch(boolean isChecked) {
                Timber.d("Setting pixel grid overlay enabled to " + isChecked);
                pixelGridEnabled.set(isChecked);
                madgeFrameLayout.setOverlayEnabled(isChecked);
                uiPixelRatioElement.setEnabled(isChecked);
            }
        };
        uiPixelGridElement.setChecked(pixelGridEnabled.get());
        addElement(uiPixelGridElement);

        uiPixelRatioElement = new ToggleElement("Pixel Scale") {
            @Override
            public void onSwitch(boolean isChecked) {
                Timber.d("Setting pixel scale overlay enabled to " + isChecked);
                pixelRatioEnabled.set(isChecked);
                madgeFrameLayout.setOverlayRatioEnabled(isChecked);
            }
        };
        uiPixelRatioElement.setChecked(pixelRatioEnabled.get());
        addElement(uiPixelRatioElement);

        attach(activity,content);
    }

    public void attach(Activity activity, ViewGroup content){
        madgeFrameLayout = new MadgeFrameLayout(activity);

        ViewGroup parent = (ViewGroup) content.getParent();
        parent.removeView(content);
        parent.addView(madgeFrameLayout,0);
        madgeFrameLayout.addView(content);

        boolean gridEnabled = pixelGridEnabled.get();
        madgeFrameLayout.setOverlayEnabled(gridEnabled);
        uiPixelGridElement.setChecked(gridEnabled);
        uiPixelRatioElement.setEnabled(gridEnabled);

        boolean ratioEnabled = pixelRatioEnabled.get();
        madgeFrameLayout.setOverlayRatioEnabled(ratioEnabled);
        uiPixelRatioElement.setChecked(ratioEnabled);
    }

}
