package com.jug6ernaut.debugdrawer.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jug6ernaut.saber.preferences.BooleanPreference;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by williamwebb on 7/2/15.
 */
public class ExpandableView extends LinearLayout {

	public final TextView  titleView;
	public final ViewGroup content;
	private final BooleanPreference isExpanded;

	private OnStateChangeListener stateChangeListener;

	public ExpandableView(Context context, TextView titleView, final ViewGroup content) {
		super(context);

		isExpanded = new BooleanPreference(context.getSharedPreferences("debugdrawer",Context.MODE_PRIVATE),titleView.getText().toString(),"true");

		this.titleView = titleView;
		this.content = content;

		this.setOrientation(VERTICAL);
		this.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

		this.addView(titleView);
		this.addView(content);

		titleView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				boolean expanded = (content.getVisibility() != VISIBLE);
				setExpanded(expanded);
				isExpanded.set(expanded);
				if(stateChangeListener != null) stateChangeListener.onStateChanged(isExpanded());
			}
		});

		setExpanded(isExpanded.get());
	}

	private void setExpanded(boolean expanded) {
		if(expanded) {
			content.setVisibility(VISIBLE);
		} else {
			content.setVisibility(GONE);
		}
	}

	public boolean isExpanded() {
		return content.getVisibility() == View.VISIBLE;
	}

	public void setOnStateChangeListener(OnStateChangeListener oscl) {
		this.stateChangeListener = oscl;
	}

	public interface OnStateChangeListener {
		void onStateChanged(boolean expanded);
	}
}
