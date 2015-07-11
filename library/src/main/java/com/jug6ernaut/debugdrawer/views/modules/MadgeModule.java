package com.jug6ernaut.debugdrawer.views.modules;

import android.app.Activity;
import com.jakewharton.madge.MadgeFrameLayout;
import com.jug6ernaut.debugdrawer.DebugView;
import com.jug6ernaut.debugdrawer.R;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.ToggleElement;
import com.jug6ernaut.saber.Preference;
import com.jug6ernaut.saber.Saber;
import com.jug6ernaut.saber.preferences.BooleanPreference;
import timber.log.Timber;

import static butterknife.ButterKnife.findById;

/**
 * Created by williamwebb on 7/2/14.
 */
public class MadgeModule extends DebugModule {

    @Preference BooleanPreference pixelGridEnabled;
    @Preference BooleanPreference pixelRatioEnabled;
    MadgeFrameLayout  madgeFrameLayout;
    ToggleElement     uiPixelGridElement;
    ToggleElement     uiPixelRatioElement;

    public MadgeModule() {
        super("UI");
    }

    @Override
    protected void onAttach(Activity activity, DebugView parent) {
        Saber.inject(this,activity);

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

        attach(activity);
    }

    public void attach(Activity activity){
        madgeFrameLayout = findById(activity, R.id.madge_container);

        boolean gridEnabled = pixelGridEnabled.get();
        madgeFrameLayout.setOverlayEnabled(gridEnabled);
        uiPixelGridElement.setChecked(gridEnabled);
        uiPixelRatioElement.setEnabled(gridEnabled);

        boolean ratioEnabled = pixelRatioEnabled.get();
        madgeFrameLayout.setOverlayRatioEnabled(ratioEnabled);
        uiPixelRatioElement.setChecked(ratioEnabled);
    }

}
