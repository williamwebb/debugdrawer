package com.jug6ernaut.debugdrawer.views.groups;

import android.app.Activity;
import android.widget.GridLayout;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.jug6ernaut.debugdrawer.R;
import com.jug6ernaut.debugdrawer.preference.BooleanPreference;
import com.jug6ernaut.debugdrawer.views.DebugGroup;
import com.jug6ernaut.debugdrawer.views.ToggleElement;
import timber.log.Timber;

import static butterknife.ButterKnife.findById;

/**
 * Created by williamwebb on 7/2/14.
 */
public class ScalpelGroup extends DebugGroup {

    ScalpelFrameLayout scalpelFrameLayout;

    ToggleElement uiScalpelElement;
    ToggleElement uiScalpelWireframeElement;

    private BooleanPreference scalpelEnabled;
    private BooleanPreference scalpelWireframeEnabled;

    public ScalpelGroup(Activity activity) {
        super("Scalpel", activity);
        scalpelEnabled = new BooleanPreference(prefs,"scalpelEnabled");
        scalpelWireframeEnabled = new BooleanPreference(prefs,"scalpelWireframeEnabled");

        uiScalpelElement = new ToggleElement("Scalpel",activity) {
            @Override
            public void onAction(Boolean isChecked) {
                Timber.d("Setting pixel scale overlay enabled to " + isChecked);
                Timber.d("Setting scalpel interaction enabled to " + isChecked);
                scalpelEnabled.set(isChecked);
                scalpelFrameLayout.setLayerInteractionEnabled(isChecked);
                uiScalpelWireframeElement.setEnabled(isChecked);
            }
        };
        addElement(uiScalpelElement);

        uiScalpelWireframeElement = new ToggleElement("Wireframe",activity) {
            @Override
            public void onAction(Boolean isChecked) {
                Timber.d("Setting scalpel wireframe enabled to " + isChecked);
                scalpelWireframeEnabled.set(isChecked);
                scalpelFrameLayout.setDrawViews(!isChecked);
            }
        };
        addElement(uiScalpelWireframeElement);
    }

    @Override
    public void onAttach(GridLayout gridLayout){
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
