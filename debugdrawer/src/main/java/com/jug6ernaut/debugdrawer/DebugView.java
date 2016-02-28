package com.jug6ernaut.debugdrawer;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.jug6ernaut.debugdrawer.views.ExpandableView;
import timber.log.Timber;

import static com.jug6ernaut.debugdrawer.utils.ViewUtils.findById;

public final class DebugView extends FrameLayout {

	public final View         contextualTitleView;
	public final LinearLayout contextualListView;

	public DebugView(Context context) {
		this(context, null);
	}

	public DebugView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Inflate all of the controls and inject them.
		LayoutInflater.from(context).inflate(R.layout.debug_view_content, this);
		contextualTitleView = findById(this, R.id.debug_contextual_title);
		contextualListView = findById(this, R.id.debug_contextual_list);

		enableLayoutTransitions(contextualListView);

		((ImageView) findById(this, R.id.debug_icon)).setImageResource(context.getApplicationInfo().icon);
	}

	public void addView(final ExpandableView expandableView) {
		contextualListView.addView(expandableView);
//		contextualListView.addDragView(expandableView, expandableView); TODO: explore reordering
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private static void enableLayoutTransitions(ViewGroup viewGroup) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			// Remove the status bar color. The DrawerLayout is responsible for drawing it from now on.
			LayoutTransition lt = new LayoutTransition();
			lt.enableTransitionType(LayoutTransition.CHANGING);
			viewGroup.setLayoutTransition(lt);
		} else {
			Timber.w("Error enabling LayoutTransitions, only supported for API14+.");
		}
	}
}
