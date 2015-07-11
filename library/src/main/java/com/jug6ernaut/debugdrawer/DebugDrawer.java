package com.jug6ernaut.debugdrawer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import com.jakewharton.scalpel.ScalpelFrameLayout;
import com.jakewharton.u2020.ui.debug.ContextualDebugActions;
import com.jakewharton.u2020.ui.debug.DebugDrawerLayout;
import com.jakewharton.u2020.ui.debug.HierarchyTreeChangeListener;
import com.jug6ernaut.debugdrawer.views.DebugElement;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.saber.Preference;
import com.jug6ernaut.saber.Saber;
import com.jug6ernaut.saber.preferences.BooleanPreference;
import com.mattprecious.telescope.TelescopeLayout;

import java.util.Hashtable;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static butterknife.ButterKnife.findById;

public final class DebugDrawer {
	@Preference BooleanPreference seenDebugDrawer;
	private Map<String, DebugModule> moduleMap = new Hashtable<>();

	public DebugDrawer modules(DebugModule... modules) {
		for (DebugModule module : modules) {
			String id = module.getTitle();
			DebugModule m = moduleMap.get(id);
			if (m == null) {
				moduleMap.put(id, module);
			} else {
				m.addModule(module);
			}
		}
		return this;
	}

	public DebugDrawer elements(String groupName, DebugElement... elements) {
		DebugModule m = moduleMap.get(groupName);
		if (m == null) {
			DebugModule module = new EmptyModule(groupName);
			module.addElement(elements);
			moduleMap.put(groupName, module);
		} else {
			m.addElement(elements);
		}
		return this;
	}

	public void bind(final Activity activity) {
		ViewGroup rootView = (ViewGroup) activity.findViewById(android.R.id.content);

		//get the content view
		View contentView = rootView.getChildAt(0);
		boolean alreadyInflated = contentView instanceof DebugDrawerLayout;

		DebugDrawerLayout mDrawerLayout = (DebugDrawerLayout) activity.getLayoutInflater().inflate(R.layout
				.debug_activity_frame, rootView, false);

		final ViewHolder viewHolder = new ViewHolder(mDrawerLayout);

		//only add the new layout if it wasn't done before
		if (!alreadyInflated) {
			// remove the contentView
			rootView.removeView(contentView);
		} else {
			//if it was already inflated we have to clean up again
			rootView.removeAllViews();
		}

		//create the layoutParams to use for the contentView
		FrameLayout.LayoutParams layoutParamsContentView = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);

		//add the contentView to the drawer content frameLayout
		viewHolder.content.addView(contentView, layoutParamsContentView);

		//add the drawerLayout to the root
		rootView.addView(mDrawerLayout, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

		loadPrefs(activity);

		final Context drawerContext = new ContextThemeWrapper(activity, R.style.Theme_U2020_Debug);
		final DebugView debugView = new DebugView(drawerContext);
		ScrollView sv = findById(activity, R.id.debug_drawer);
		debugView.contextualListView.setContainerScrollView(sv);

		viewHolder.debugDrawer.addView(debugView);

		// Set up the contextual actions to watch views coming in and out of the content area.
		ContextualDebugActions contextualActions = debugView.getContextualDebugActions();
		contextualActions.setActionClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewHolder.drawerLayout.closeDrawers();
			}
		});
		viewHolder.content.setOnHierarchyChangeListener(HierarchyTreeChangeListener.wrap(contextualActions));

		viewHolder.drawerLayout.setDrawerShadow(R.drawable.debug_drawer_shadow, Gravity.RIGHT);

		viewHolder.drawerLayout.setDrawerListener(new DebugDrawerLayout.DrawerListener() {
			@Override public void onDrawerSlide(View drawerView, float slideOffset) { }
			@Override public void onDrawerStateChanged(int newState) { }

			@Override
			public void onDrawerOpened(View drawerView) {
				for(DebugModule group : moduleMap.values()){
					group.onDrawerOpened();
				}
			}
			@Override
			public void onDrawerClosed(View drawerView) {
				for(DebugModule group : moduleMap.values()){
					group.onDrawerClosed();
				}
			}
		});


		// Attach all modules to the DebugView
		for(DebugModule m : moduleMap.values()) {
			m.attach(activity,debugView);
		}

		// If you have not seen the debug drawer before, show it with a message
		if (!seenDebugDrawer.get()) {
			viewHolder.drawerLayout.postDelayed(new Runnable() {
				@Override public void run() {
					viewHolder.drawerLayout.openDrawer(Gravity.RIGHT);
					Toast.makeText(drawerContext, R.string.debug_drawer_welcome, Toast.LENGTH_LONG).show();
				}
			}, 1000);
			seenDebugDrawer.set(true);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			// Remove the status bar color. The DrawerLayout is responsible for drawing it from now on.
			setStatusBarColor(activity.getWindow());
		}
	}

	private void loadPrefs(Context context){
		Saber.inject(this,context);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static void setStatusBarColor(Window window) {
		window.setStatusBarColor(Color.TRANSPARENT);
	}

	static class ViewHolder {
		DebugDrawerLayout  drawerLayout;
		ViewGroup          debugDrawer;
		TelescopeLayout    telescopeLayout;
		ScalpelFrameLayout content;

		public ViewHolder(View view) {
			drawerLayout = findById(view, R.id.debug_drawer_layout);
			debugDrawer = findById(view, R.id.debug_drawer);
			telescopeLayout = findById(view, R.id.telescope_container);
			content = findById(view, R.id.debug_content);
		}
	}

	private class EmptyModule extends DebugModule {

		public EmptyModule(String title) {
			super(title);
		}
		@Override public void onAttach(Activity activity, DebugView parent) { }
	}
}
