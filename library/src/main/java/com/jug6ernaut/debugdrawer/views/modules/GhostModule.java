package com.jug6ernaut.debugdrawer.views.modules;

import android.app.Activity;
import com.jug6ernaut.debugdrawer.DebugView;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.elements.GhostElement;

/**
 * Created by williamwebb on 7/2/14.
 */
public class GhostModule extends DebugModule {

    public GhostModule() {
        super("LogCat");
    }

    @Override
    protected void onAttach(Activity activity, DebugView parent) {
        GhostElement ghostLog = new GhostElement(activity);

        addElement(ghostLog);

        if(ghostLog.isGhostEnabled())ghostLog.start();
    }
}
