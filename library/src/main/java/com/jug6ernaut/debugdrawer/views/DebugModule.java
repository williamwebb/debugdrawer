package com.jug6ernaut.debugdrawer.views;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.jug6ernaut.debugdrawer.DebugView;
import com.jug6ernaut.debugdrawer.R;
import com.jug6ernaut.debugdrawer.utils.ActivityEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by williamwebb on 6/28/14.
 */
public abstract class DebugModule {
    private final String             title;
    private final List<DebugElement> elements   = new ArrayList<>();
    private final List<DebugModule>  subModules = new ArrayList<>();

    private DebugView parent;
    private ExpandableView view;

    public DebugModule(String title) {
        this.title = title;
    }

    protected abstract void onAttach(Activity activity, DebugView parent);

    private void init(Activity activity, DebugView parent) {
        this.parent = parent; // TODO: Verify submodules need this set
        registerActivityListener(activity);
    }

    public void addElement(DebugElement... element) {
        for (DebugElement de : element) {
            elements.add(de);
        }
    }

    public void addModule(DebugModule... module) {
        for (DebugModule dm : module) {
            subModules.add(dm);
        }
    }

    public List<DebugElement> getElements() {
        return elements;
    }

    private void _attach(DebugModule module, Activity activity, DebugView parent) {
        module.init(activity,parent); // Internal setup
        module.onAttach(activity, parent); // External setup
    }

    public void attach(Activity activity, DebugView parent) {
        // init this module and all subModules
        _attach(this,activity,parent);
        for(DebugModule dm : subModules) {
            _attach(dm,activity,parent);
        }

        TextView header = createHeader(activity);
        ViewGroup children = addChildren(parent);

        view = new ExpandableView(parent.getContext(),header,children);
        parent.addView(view);

        notifyModuleAttached(activity,this);
        for(DebugModule dm : subModules) {
            notifyModuleAttached(activity,dm);
        }
    }

    private TextView createHeader(Activity activity) {
        TextView titleView = new TextView(new ContextThemeWrapper(activity, R.style.Widget_U2020_DebugDrawer_Header));
        titleView.setText(title);
        int dp02 = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, 2, activity.getResources().getDisplayMetrics());
        titleView.setPadding(0,dp02,0,dp02);

        LayoutParams lp = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        titleView.setLayoutParams(lp);
        return titleView;
    }

    private ViewGroup addChildren(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LinearLayout ll = new LinearLayout(parent.getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LayoutParams(MATCH_PARENT,WRAP_CONTENT));

        DisplayMetrics metrics = parent.getContext().getResources().getDisplayMetrics();
        int dp02 = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, 2, metrics);
        int dp05 = (int) TypedValue.applyDimension(COMPLEX_UNIT_DIP, 5, metrics);
        ll.setPadding(dp05,dp02,0,0);

        // Attach this module elements and subModule elements
        attachElements(inflater,parent,ll,elements);
        for(DebugModule subModule : subModules) {
            attachElements(inflater,parent,ll,subModule.getElements());
        }

        return ll;
    }

    private void notifyModuleAttached(Activity activity, DebugModule debugModule) {
        for (DebugElement e : debugModule.elements) {
            e.onModuleAttached(activity,this);
        }
    }

    private void attachElements(LayoutInflater inflater, ViewGroup parent, ViewGroup toAdd, Collection<DebugElement> debugElements) {
        for (DebugElement e : debugElements) {
            View view = e.create(this,inflater, parent);
            toAdd.addView(view);
        }
    }

    private void registerActivityListener(Activity activity) {
        // Cleans up after itself, no need to unregister
        new ActivityEventListener(activity) {
            @Override
            public void onStart(Activity activity) {
                postEvent(DrawerEvent.ACTIVITY_START);
            }

            public void onStopped(Activity activity) {
                postEvent(DrawerEvent.ACTIVITY_STOP);
            }
        }.register(activity.getApplication());
    }

    public String getTitle() {
        return title;
    }

    public ExpandableView getView() {
        return view;
    }

    public void onDrawerOpened() {
        postEvent(DrawerEvent.OPENED);
    }

    public void onDrawerClosed() {
        postEvent(DrawerEvent.CLOSED);
    }

    public DebugView getParent() {
        return parent;
    }

    enum DrawerEvent {
        OPENED,
        CLOSED,
        ACTIVITY_START,
        ACTIVITY_STOP
    }

    private void postEvent(DrawerEvent event) {
        switch (event) {
            case OPENED: for (DebugElement e : elements) e.onDrawerOpened(); break;
            case CLOSED: for (DebugElement e : elements) e.onDrawerClosed(); break;
            case ACTIVITY_START: for (DebugElement e : elements) e.onActivityStart(); break;
            case ACTIVITY_STOP: for (DebugElement e : elements) e.onActivityStop(); break;
        }
    }
}
