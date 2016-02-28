package com.jug6ernaut.debugdrawer.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by williamwebb on 1/31/16.
 */
public abstract class ButtonElement extends DebugElement {

	private final String buttonText;

	public ButtonElement(String buttonText) {
		this.buttonText = buttonText;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root) {
		Button button = new Button(root.getContext());
		button.setText(buttonText);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ButtonElement.this.onClick(v);
			}
		});
		return button;
	}

	public abstract void onClick(View view);
}
