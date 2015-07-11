package com.jug6ernaut.debugdrawer.views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.jug6ernaut.debugdrawer.R;
import com.jug6ernaut.saber.Preference;
import com.jug6ernaut.saber.Saber;
import com.jug6ernaut.saber.preferences.StringPreference;

/**
 * Created by williamwebb on 6/28/14.
 */
public abstract class SpinnerElement extends DebugElement {
    private final String               name;
    private final String[]             elements;
    private       Spinner              spinner;
    private       ArrayAdapter<String> adapter;
    private       boolean              rememberState = true;

    @Preference StringPreference currentValue;

    public SpinnerElement(String name, String[] elements) {
        this.name = name;
        this.elements = elements;
    }
    public abstract void onItemSelect(String item);

    @Override
    public View onCreateView(DebugModule parent, LayoutInflater inflater, ViewGroup root) {
        Context context = root.getContext();
        Saber.inject(this, context);

        spinner = (Spinner) inflater.inflate(R.layout.debug_template_spinner, null);
        adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, elements); //selected item will
        // look like a spinner set from XML
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setGravity(Gravity.START | Gravity.END | Gravity.CENTER_VERTICAL); // "start|end|center_vertical"
        spinner.setAdapter(adapter);

        // used to prevent the onItemSelectedListener from firing automatically
        if(currentValue.isSet() && rememberState) {
            spinner.setSelection(adapter.getPosition(currentValue.get()),false);
        } else {
            spinner.setSelection(0,false);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onItemSelect(elements[position]);
                currentValue.set(elements[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

	    TextView nameView = new TextView(context);
		nameView.setText(name);
        return TextElement.createDefaultLayout(nameView,spinner);
    }

    public void setRememberState(boolean shouldRemember) {
        this.rememberState = shouldRemember;
    }

    public void setSelection(String selection) {
        if(spinner != null) {
            spinner.setSelection(adapter.getPosition(selection));
        }
    }

}
