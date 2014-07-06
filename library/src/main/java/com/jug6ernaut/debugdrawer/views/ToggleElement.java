package com.jug6ernaut.debugdrawer.views;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.jug6ernaut.debugdrawer.R;

/**
 * Created by williamwebb on 6/28/14.
 */
public abstract class ToggleElement extends DebugElement<Boolean,Switch>{

    private Boolean checked = false;

    public ToggleElement(String name, Context context) {
        super(context,name);
    }
    public ToggleElement(String name, Context context, boolean checked) {
        super(context,name);
        this.checked = checked;
    }

    protected Switch createView(){
//        Switch swi = (Switch) LayoutInflater.from(context).inflate(R.layout.debug_template_switch,null);
        Switch swi = new Switch(new ContextThemeWrapper(context,R.style.Widget_U2020_DebugDrawer_RowWidget));
        swi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onAction(isChecked);
            }
        });
        swi.setGravity(Gravity.START | Gravity.END | Gravity.CENTER_VERTICAL); // "start|end|center_vertical"
        swi.setChecked(isChecked());
        return swi;
    }

    public void setChecked(boolean checked){
        this.checked = checked;
        if(getActionView() != null)
            getActionView().setChecked(checked);
    }

    public Boolean isChecked(){
        if(getActionView() == null) return checked;
        else return getActionView().isChecked();
    }
}
