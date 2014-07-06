package com.jug6ernaut.debugdrawer;

import android.app.Activity;
import android.view.View;
import com.jug6ernaut.debugdrawer.views.DebugGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by williamwebb on 6/2/14.
 */
public class DebugDrawer {


    private static Map<Activity,DebugDrawerHolder> drawers = new ConcurrentHashMap<Activity,DebugDrawerHolder>();

    private static List<DebugGroup> views = new ArrayList<DebugGroup>();

    public static void addGroup(DebugGroup group){
        views.add(group);
    }

    public static void attach(Activity activity, int contentView){
        if(!drawers.containsKey(activity)){
            DebugDrawerHolder debugDrawer = new DebugDrawerHolder(activity,contentView);
            for(DebugGroup v : views) debugDrawer.addGroup(v);
            drawers.put(activity,debugDrawer);
        }
    }

    public static void attach(Activity activity, View contentView){
        if(!drawers.containsKey(activity)){
            DebugDrawerHolder debugDrawer = new DebugDrawerHolder(activity,contentView);
            for(DebugGroup v : views) debugDrawer.addGroup(v);
            drawers.put(activity,debugDrawer);
        }
    }

}
