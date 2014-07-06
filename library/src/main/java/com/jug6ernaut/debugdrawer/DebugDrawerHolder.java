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
import android.widget.LinearLayout;
import android.widget.Toast;
import com.jug6ernaut.debugdrawer.preference.BooleanPreference;
import com.jug6ernaut.debugdrawer.views.DebugGroup;
import com.jug6ernaut.debugdrawer.views.groups.*;

import java.util.Collections;
import java.util.Set;

import static butterknife.ButterKnife.findById;


/**
 * Created by williamwebb on 6/2/14.
 */
public class DebugDrawerHolder {

    private BooleanPreference seenDebugDrawer;

    DrawerLayout drawerLayout;
    LinearLayout contextualListView;
    GridLayout debugAdditional;
    ViewGroup content;
    View contextualTitleView;

    Activity drawerContext;

    public DebugDrawerHolder(Activity activity, View contentView){
        init(activity);
        content.addView(contentView);
    }

    public DebugDrawerHolder(Activity activity, int contentView){
        init(activity);
        activity.getLayoutInflater().inflate(contentView, content);
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
        setupGhostSection();
        setupUserInterfaceSection();
        setupDeviceSection();
        setupBuildSection();

        if (!seenDebugDrawer.get() || true) {
            drawerLayout.postDelayed(new Runnable() {
                @Override public void run() {
                    drawerLayout.openDrawer(Gravity.END);
                    Toast.makeText(activity, R.string.debug_drawer_welcome, Toast.LENGTH_LONG).show();
                }
            }, 1000);
            seenDebugDrawer.set(true);
        }
    }

    private void setupAdditional(){
        debugAdditional = findById(drawerContext,R.id.debug_root);
    }

    public void addGroup(DebugGroup group){
        group.attach(debugAdditional);
    }

    private void setupGhostSection(){
        addGroup(new GhostGroup(drawerContext));
    }

    private void setupBuildSection() {
        addGroup(new BuildGroup(drawerContext));
    }

    private void setupDeviceSection() {
        addGroup(new DeviceInfoGroup(drawerContext));
    }

    private void setupUserInterfaceSection() {
        addGroup(new ScalpelGroup(drawerContext));
        addGroup(new MadgeGroup(drawerContext));
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
    }

}
