package com.jug6ernaut.debugdrawer.views;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import com.jug6ernaut.debugdrawer.R;

/**
 * Created by williamwebb on 6/28/14.
 */
public abstract class DebugElement<T,V extends View> {
    protected final Context context;
    private String name;
    V actionView;
    TextView titleView;
    boolean isEnabled = true;

    public DebugElement(Context context, String name){
        this.context = context;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public abstract void onAction(T t);
    protected abstract V createView();

    public void init(GridLayout gridLayout){
//        TextView title = (TextView) LayoutInflater.from(context).inflate(R.layout.debug_template_title,null);
        titleView = new TextView(new ContextThemeWrapper(context,R.style.Widget_U2020_DebugDrawer_RowTitle));
        titleView.setTextAppearance(context, R.style.Widget_U2020_DebugDrawer_RowTitle);
        titleView.setGravity(Gravity.START | Gravity.END | Gravity.CENTER_VERTICAL); // "start|end|center_vertical"
        titleView.setText(name);

        actionView = createView();
        actionView.setEnabled(isEnabled());
    }

    public V getActionView(){
        return actionView;
    }

    public TextView getTitleView(){
        return titleView;
    }

    public void attach(GridLayout gridLayout){
        init(gridLayout);
        gridLayout.addView(getTitleView());
        gridLayout.addView(getActionView());
    }

    public void setEnabled(boolean enable){
        this.isEnabled = enable;
        if(actionView != null)
            actionView.setEnabled(enable);
    }

    public Boolean isEnabled(){
        return isEnabled;
    }
}
