package com.jug6ernaut.debugdrawer.views;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.TextView;
import com.jug6ernaut.debugdrawer.R;

/**
 * Created by williamwebb on 6/28/14.
 */
public class TextElement extends DebugElement<Void,TextView> {
    private String value = "";

    public TextElement(Context context, String name, String value) {
        super(context, name);
        this.value = value;
    }

    public TextElement(Context context, String name) {
        super(context, name);
    }

    public void setValue(String value){
        this.value = value;
        if(getActionView() != null) getActionView().setText(value);
    }

    public String getValue(){
        return value;
    }

    @Override
    public void onAction(Void o) {}

    @Override
    public TextView createView() {
        TextView tx = new TextView(new ContextThemeWrapper(context,R.style.Widget_U2020_DebugDrawer_RowValue));
        tx.setGravity(Gravity.START | Gravity.END | Gravity.CENTER_VERTICAL); // "start|end|center_vertical"
        tx.setText(value);
        return tx;
    }
}
