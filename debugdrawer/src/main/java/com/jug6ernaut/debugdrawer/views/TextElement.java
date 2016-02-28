package com.jug6ernaut.debugdrawer.views;

import android.view.*;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jug6ernaut.debugdrawer.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by williamwebb on 6/28/14.
 */
public class TextElement extends DebugElement {
    private String name;
    private String value;
    private TextView nameView;
    private TextView valueView;

    public TextElement(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup root) {
        nameView = new TextView(new ContextThemeWrapper(root.getContext(), R.style.Widget_U2020_DebugDrawer_RowTitle));
        nameView.setText(name);
        valueView = new TextView(new ContextThemeWrapper(root.getContext(), R.style.Widget_U2020_DebugDrawer_RowValue));
        valueView.setText(value);
        valueView.setGravity(Gravity.END);

        return createDefaultLayout(nameView,valueView);
    }

    public void setValue(String value) {
        this.value = value;
        if(valueView != null) valueView.setText(value);
    }

    public static LinearLayout createDefaultLayout(View view1, View view2) {
        LinearLayout ll = new LinearLayout(view1.getContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        lp.weight = 1;
        ll.addView(view1,lp);
        lp.weight = 1;
        ll.addView(view2,lp);

        return ll;
    }

    public TextView getNameView() {
        return nameView;
    }
    public TextView getValueView() {
        return valueView;
    }
}
