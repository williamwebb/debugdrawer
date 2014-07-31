package com.jug6ernaut.debugdrawer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.jug6ernaut.debugdrawer.preference.BooleanPreference;
import com.jug6ernaut.debugdrawer.views.DebugGroup;
import com.jug6ernaut.debugdrawer.views.groups.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static butterknife.ButterKnife.findById;


/**
 * Created by williamwebb on 6/2/14.
 */
public class DebugDrawer {

    private BooleanPreference seenDebugDrawer;

    DrawerLayout drawerLayout;
    LinearLayout contextualListView;
    GridLayout debugAdditional;
    ViewGroup content;
    View contextualTitleView;

    Activity drawerContext;

    List<DebugGroup> additionalDebugGroups = new ArrayList<>();

    public DebugDrawer(Activity activity){
        this(activity, false);
    }

    public DebugDrawer(Activity activity, boolean addDefaultGroups){
        this.drawerContext = activity;
        if(addDefaultGroups){
            addDefaultGroups();
        }
    }

    public void attach(View contentView){
        init(drawerContext);
        content.addView(contentView);
        postAttach();
    }

    public void attach(int contentView){
        init(drawerContext);
        drawerContext.getLayoutInflater().inflate(contentView, content);
        postAttach();
    }

    private void init(final Activity activity){
        this.drawerContext = activity;
        activity.setContentView(R.layout.debug_activity_frame);

        ViewGroup drawer = findById(activity, R.id.debug_drawer);
        LayoutInflater.from(activity).inflate(R.layout.debug_drawer_content, drawer);

        ViewGroup content = findById(activity,R.id.debug_content);

        // Set up the contextual actions to watch views coming in and out of the content area.
        Set<ContextualDebugActions.DebugAction<?>> debugActions = Collections.emptySet();
        ContextualDebugActions contextualActions = new ContextualDebugActions(this, debugActions);
        content.setOnHierarchyChangeListener(HierarchyTreeChangeListener.wrap(contextualActions));
        setupAdditional();

        loadPrefs();
        loadViews();

        for(DebugGroup group : additionalDebugGroups){
            group.attach(debugAdditional);
        }

        if (!seenDebugDrawer.get()) {
            drawerLayout.postDelayed(new Runnable() {
                @Override public void run() {
                    drawerLayout.openDrawer(Gravity.END);
                    Toast.makeText(activity, R.string.debug_drawer_welcome, Toast.LENGTH_LONG).show();
                }
            }, 1000);
            seenDebugDrawer.set(true);
        }
    }

    private void addDefaultGroups(){
        addDebugGroup(new GhostGroup(drawerContext));
        addDebugGroup(new ScalpelGroup(drawerContext));
        addDebugGroup(new MadgeGroup(drawerContext));
        addDebugGroup(new DeviceInfoGroup(drawerContext));
        addDebugGroup(new BuildGroup(drawerContext));
    }

    private void setupAdditional(){
        debugAdditional = findById(drawerContext,R.id.debug_root);
    }

    public void addDebugGroup(DebugGroup group){
        additionalDebugGroups.add(group);
    }

    private void loadPrefs(){
        SharedPreferences prefs = drawerContext.getSharedPreferences("debug_prefs", Context.MODE_PRIVATE);
        seenDebugDrawer = new BooleanPreference(prefs,"seenDebugDrawer");
    }

    void loadViews(){
        drawerLayout = findById(drawerContext, R.id.debug_drawer_layout);
        content = findById(drawerContext, R.id.debug_content);

        contextualTitleView = findById(drawerContext, R.id.debug_contextual_title);
        contextualListView = findById(drawerContext, R.id.debug_contextual_list);

        ((ImageView)findById(drawerContext, R.id.debug_icon)).setImageResource(drawerContext.getApplicationInfo().icon);
    }

    protected void postAttach(){};

}
