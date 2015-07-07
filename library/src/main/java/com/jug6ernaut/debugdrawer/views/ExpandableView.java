package com.jug6ernaut.debugdrawer.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by williamwebb on 7/2/15.
 */
public class ExpandableView extends LinearLayout {

	public final TextView  titleView;
	public final ViewGroup content;
	private OnStateChangeListener stateChangeListener;

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public ExpandableView(Context context, TextView titleView, final ViewGroup content) {
		super(context);

		this.titleView = titleView;
		this.content = content;

		this.setOrientation(VERTICAL);
		this.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

		this.addView(titleView);
		this.addView(content);

		titleView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(content.getVisibility() == GONE) {
					content.setVisibility(VISIBLE);
				} else {
					content.setVisibility(GONE);
				}

				if(stateChangeListener != null)stateChangeListener.onStateChanged(isExpanded());
			}
		});
	}

	public boolean isExpanded() {
		return content.getVisibility() == View.VISIBLE;
	}

	public void setOnStateChangeListener(OnStateChangeListener oscl) {
		this.stateChangeListener = oscl;
	}

	public static interface OnStateChangeListener {
		void onStateChanged(boolean expanded);
	}
}
