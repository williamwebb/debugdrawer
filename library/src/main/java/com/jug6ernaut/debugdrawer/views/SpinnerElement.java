package com.jug6ernaut.debugdrawer.views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.jug6ernaut.debugdrawer.R;

/**
 * Created by williamwebb on 6/28/14.
 */
public abstract class SpinnerElement extends DebugElement<String,Spinner> {
    private String[] elements;
    private boolean isFirst = true; // used to skip first event that is always fired...

    public SpinnerElement(Context context, String name, String[] elements) {
        super(context, name);
        this.elements = elements;
    }

    public SpinnerElement(Context context, String name, int arrayResource) {
        super(context, name);
        this.elements = context.getResources().getStringArray(arrayResource);
    }

    @Override
    protected Spinner createView() {
//        if(args.length == 0) throw new IllegalArgumentException("Must provide element list");
//        if(args[0].getClass().equals(int.class) || args[0].getClass().equals(Integer.class)){
//            elements = context.getResources().getStringArray((Integer) args[0]);
//        } else {
//            elements = (String[])args[0];
//        }

        Spinner spinner = (Spinner) LayoutInflater.from(context).inflate(R.layout.debug_template_spinner,null);
//        Spinner spinner = new Spinner(new ContextThemeWrapper(context,R.style.Widget_U2020_DebugDrawer_RowWidget));
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, elements); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setGravity(Gravity.START | Gravity.END | Gravity.CENTER_VERTICAL); // "start|end|center_vertical"
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!isFirst) onAction(elements[position]);
                else isFirst = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return spinner;
    }
}
