package com.jug6ernaut.debugdrawer.views;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.annimon.stream.Stream;
import com.jug6ernaut.debugdrawer.DebugView;
import com.jug6ernaut.debugdrawer.R;
import com.jug6ernaut.debugdrawer.utils.ActivityEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by williamwebb on 6/28/14.
 */
public abstract class DebugModule {
    private final List<DebugElement> elements   = new ArrayList<>();
    private final List<DebugModule>  subModules = new ArrayList<>();
    private final String             title;

    private DebugView parent;
    private ViewGroup content;
    private ExpandableView view;

    public DebugModule(String title) {
        this.title = title;
    }

    protected abstract void onAttach(Activity activity, DebugView parent, ViewGroup content);

    private void init(Activity activity, DebugView parent, ViewGroup content) {
        this.parent = parent; // TODO: Verify sub-modules need this set
        this.content = content;
        registerActivityListener(activity);
    }

    public void addElement(DebugElement... element) {
        Collections.addAll(elements, element);
    }

    public void addModule(DebugModule... module) {
        Collections.addAll(subModules, module);
    }

    public List<DebugElement> getElements() {
        return elements;
    }

    private void _attach(DebugModule module, Activity activity, DebugView parent, ViewGroup content) {
        module.init(activity,parent,content); // Internal setup
        module.onAttach(activity, parent, content); // External setup
    }

    public void attach(Activity activity, DebugView parent, ViewGroup content) {
        // init this module and all subModules
        _attach(this,activity,parent,content);
        for(DebugModule dm : subModules) {
            _attach(dm,activity,parent, content);
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

    private ViewGroup addChildren(DebugView parent) {
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
        Stream.of(debugModule.elements).forEach(e -> e.onModuleAttached(activity,this));
    }

    private void attachElements(LayoutInflater inflater, DebugView parent, ViewGroup toAdd, Collection<DebugElement> debugElements) {
        Stream.of(debugElements).forEach(e -> toAdd.addView(e.create(this,inflater, parent)));
    }

    private void registerActivityListener(Activity activity) {
        // Cleans up after itself, no need to unregister
        new ActivityEventListener(activity) {
            @Override public void onStart(Activity activity) {
                postEvent(DrawerEvent.ACTIVITY_START);
            }
            @Override public void onStopped(Activity activity) {
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

    public ViewGroup getContent() {
        return content;
    }

    enum DrawerEvent {
        OPENED,
        CLOSED,
        ACTIVITY_START,
        ACTIVITY_STOP
    }

    private void postEvent(DrawerEvent event) {
        switch (event) {
            case OPENED: Stream.of(elements).forEach(DebugElement::onDrawerOpened); break;
            case CLOSED: Stream.of(elements).forEach(DebugElement::onDrawerClosed); break;
            case ACTIVITY_START: Stream.of(elements).forEach(DebugElement::onActivityStart); break;
            case ACTIVITY_STOP: Stream.of(elements).forEach(DebugElement::onActivityStop); break;
        }
    }
}
