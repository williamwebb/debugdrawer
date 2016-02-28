package com.jug6ernaut.debugdrawer.modules;

import android.app.Activity;
import android.view.ViewGroup;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.jug6ernaut.debugdrawer.DebugView;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.ToggleElement;
import com.jug6ernaut.saber.Saber;
import com.jug6ernaut.saber.preferences.Preference;
import saber.Bind;
import timber.log.Timber;

/**
 * Created by williamwebb on 7/2/14.
 */
public class ScalpelModule extends DebugModule {

    ScalpelFrameLayout scalpelFrameLayout;

    ToggleElement uiScalpelElement;
    ToggleElement uiScalpelWireframeElement;

    @Bind(key = "") Preference<Boolean> scalpelEnabled;
    @Bind(key = "") Preference<Boolean> scalpelWireframeEnabled;

    public ScalpelModule() {
        super("UI");
    }

    public ScalpelModule(String moduleName) {
        super(moduleName);
    }

    @Override
    protected void onAttach(Activity activity, DebugView parent, ViewGroup content) {
        Saber.bind(this,activity);

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

        attach(activity,content);
    }

    public void attach(Activity activity, ViewGroup content){
        scalpelFrameLayout = new ScalpelFrameLayout(activity);

        ViewGroup parent = (ViewGroup) content.getParent();
        parent.removeView(content);
        parent.addView(scalpelFrameLayout,0);
        scalpelFrameLayout.addView(content);

        boolean scalpel = scalpelEnabled.get();
        scalpelFrameLayout.setLayerInteractionEnabled(scalpel);
        uiScalpelElement.setChecked(scalpel);
        uiScalpelWireframeElement.setEnabled(scalpel);

        boolean wireframe = scalpelWireframeEnabled.get();
        scalpelFrameLayout.setDrawViews(!wireframe);
        uiScalpelWireframeElement.setChecked(wireframe);
    }

}
