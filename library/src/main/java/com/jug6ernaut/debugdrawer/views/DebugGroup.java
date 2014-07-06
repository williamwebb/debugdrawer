package com.jug6ernaut.debugdrawer.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;
import com.jug6ernaut.debugdrawer.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by williamwebb on 6/28/14.
 */
public class DebugGroup {
    protected final SharedPreferences prefs;
    private String title;
    private Context context;
    private List<DebugElement> elements = new ArrayList<DebugElement>();

    public DebugGroup(String title, Context context){
        this.title = title;
        this.context = context;
        prefs = context.getSharedPreferences(title+"prefs",Context.MODE_PRIVATE);
    }

    public DebugGroup(int title, Context context){
        this(context.getString(title),context);
    }

    public void addElement(DebugElement element){
        elements.add(element);
    }

    public void attach(GridLayout layout){
//        TextView titleView = (TextView) LayoutInflater.from(context).inflate(R.layout.debug_template_header,null);
        TextView titleView = new TextView(new ContextThemeWrapper(context, R.style.Widget_U2020_DebugDrawer_Header));
        titleView.setText(title);
        int dp10 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
        int dp05 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
        titleView.setPadding(0,dp10,0,dp05);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.columnSpec = GridLayout.spec(0,2);
        lp.setGravity(Gravity.START | Gravity.END);
        layout.addView(titleView,lp);
        for(DebugElement element : elements){
            element.attach(layout);
        }
    }
}
