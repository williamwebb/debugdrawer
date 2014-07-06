package com.jug6ernaut.debugdrawer.views.groups;

import android.app.Activity;
import com.jug6ernaut.debugdrawer.views.DebugGroup;

/**
 * Created by williamwebb on 7/2/14.
 */
public class GhostGroup extends DebugGroup {

    public GhostGroup(Activity context) {
        super("LogCat", context);

        GhostElement ghostLog = new GhostElement(context);

        addElement(ghostLog);

        if(ghostLog.isGhostEnabled())ghostLog.start();
    }
}
