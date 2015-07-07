package com.jug6ernaut.debugdrawer.views.modules;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.jug6ernaut.debugdrawer.DebugView;
import com.jug6ernaut.debugdrawer.R;
import com.jug6ernaut.debugdrawer.preference.BooleanPreference;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.ToggleElement;
import timber.log.Timber;

import static butterknife.ButterKnife.findById;

/**
 * Created by williamwebb on 7/2/14.
 */
public class ScalpelModule extends DebugModule {

    ScalpelFrameLayout scalpelFrameLayout;

    ToggleElement uiScalpelElement;
    ToggleElement uiScalpelWireframeElement;

    private BooleanPreference scalpelEnabled;
    private BooleanPreference scalpelWireframeEnabled;

    public ScalpelModule() {
        super("UI");
    }

    @Override
    protected void onAttach(Activity activity, DebugView parent) {
        SharedPreferences prefs = activity.getSharedPreferences(getTitle() + "_prefs", Context.MODE_PRIVATE);

        scalpelEnabled = new BooleanPreference(prefs,"scalpelEnabled");
        scalpelWireframeEnabled = new BooleanPreference(prefs,"scalpelWireframeEnabled");

        uiScalpelElement = new ToggleElement("Scalpel") {
            @Override
            public void onSwitch(boolean isChecked) {
                Timber.d("Setting pixel scale overlay enabled to " + isChecked);
                Timber.d("Setting scalpel interaction enabled to " + isChecked);
                scalpelEnabled.set(isChecked);
                scalpelFrameLayout.setLayerInteractionEnabled(isChecked);
                uiScalpelWireframeElement.setEnabled(isChecked);
            }
        };
        addElement(uiScalpelElement);

        uiScalpelWireframeElement = new ToggleElement("Wireframe") {
            @Override
            public void onSwitch(boolean isChecked) {
                Timber.d("Setting scalpel wireframe enabled to " + isChecked);
                scalpelWireframeEnabled.set(isChecked);
                scalpelFrameLayout.setDrawViews(!isChecked);
            }
        };
        addElement(uiScalpelWireframeElement);

        attach(activity);
    }

    public void attach(Activity activity){
        scalpelFrameLayout = findById(activity, R.id.debug_content);

        boolean scalpel = scalpelEnabled.get();
        scalpelFrameLayout.setLayerInteractionEnabled(scalpel);
        uiScalpelElement.setChecked(scalpel);
        uiScalpelWireframeElement.setEnabled(scalpel);

        boolean wireframe = scalpelWireframeEnabled.get();
        scalpelFrameLayout.setDrawViews(!wireframe);
        uiScalpelWireframeElement.setChecked(wireframe);
    }

}
