package com.jug6ernaut.debugdrawer.views;

import android.content.Context;
import android.view.*;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.jug6ernaut.debugdrawer.R;
import com.jug6ernaut.debugdrawer.preference.BooleanPreference;

/**
 * Created by williamwebb on 6/28/14.
 */
public abstract class ToggleElement extends DebugElement {

    private       Switch aSwitch;
    private final String title;
    private BooleanPreference checked;
    private boolean defaultValue;

    public ToggleElement(String title) {
        this(title, false, true);
    }

    public ToggleElement(String title, boolean checked, boolean enabled) {
        this.title = title;
        this.defaultValue = checked;
        setEnabled(enabled);
    }

    public abstract void onSwitch(boolean state);

    @Override
    public View onCreateView(DebugModule parent, LayoutInflater inflater, ViewGroup root) {
        final Context context = root.getContext();
        checked = new BooleanPreference(
                context.getSharedPreferences(parent.getTitle(),Context.MODE_PRIVATE),
                getTitle(),
                defaultValue);

        aSwitch = new Switch(new ContextThemeWrapper(context, R.style.Widget_U2020_DebugDrawer_RowWidget));
        aSwitch.setText(title);
        aSwitch.setChecked(checked.get());
        aSwitch.setEnabled(isEnabled());
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked.set(isChecked);
                onSwitch(isChecked);
            }
        });
        aSwitch.setGravity(Gravity.START | Gravity.END | Gravity.CENTER_VERTICAL); // "start|end|center_vertical"
        return aSwitch;
    }

    public void setChecked(boolean checked) {
        if( this.checked != null ) this.checked.set(checked);
        if(aSwitch != null) aSwitch.setChecked(checked);
        else defaultValue = checked;
    }

    public boolean isChecked(){
        if(aSwitch != null) return aSwitch.isChecked();
        else return defaultValue;
    }

    public String getTitle() {
        return title;
    }
}
