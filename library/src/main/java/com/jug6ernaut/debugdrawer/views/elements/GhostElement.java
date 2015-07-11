package com.jug6ernaut.debugdrawer.views.elements;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import com.jug6ernaut.debugdrawer.R;
import com.jug6ernaut.debugdrawer.views.DebugElement;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.saber.Preference;
import com.jug6ernaut.saber.Saber;
import com.jug6ernaut.saber.preferences.BooleanPreference;
import com.readystatesoftware.ghostlog.GhostLogSettingsFragment;
import com.readystatesoftware.ghostlog.ServerlessLogScreen;

/**
 * Created by williamwebb on 6/29/14.
 */
public class GhostElement extends DebugElement {

    private final Activity activity;
    ServerlessLogScreen logScreen;
    final String GHOST_PREFS_TAG = "ghost_prefs";
    @Preference(GHOST_PREFS_TAG) BooleanPreference ghostEnabled;
    Switch toggle;

    public GhostElement(Activity activity, int x, int y, int gravity) {
        Saber.inject(this,activity);
        this.activity = activity;
        this.logScreen = new ServerlessLogScreen(activity, x, y, gravity);
    }

    public GhostElement(Activity activity) {
        this(activity, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0);
    }

    public void start() {
        ghostEnabled.set(true);
        if (!logScreen.isRunning()) {
            logScreen.start();
        }
        if (toggle != null && !toggle.isChecked()) toggle.setChecked(true);
    }

    public void stop() {
        ghostEnabled.set(false);
        logScreen.stop();
        if(toggle != null  && toggle.isChecked()) toggle.setChecked(false);
    }

    public boolean isGhostEnabled(){
        return ghostEnabled.get();
    }

    @Override
    public View onCreateView(DebugModule parent, LayoutInflater inflater, ViewGroup root) {
        View layout = inflater.inflate(R.layout.debug_template_ghost_element,null);
        toggle = (Switch) layout.findViewById(R.id.debug_ui_ghost_log);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                System.out.println("Click: " + b);
                if (b) {
                    start();
                } else {
                    stop();
                }
            }
        });

        ImageButton settings = (ImageButton) layout.findViewById(R.id.debug_ui_ghost_log_edit);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GhostLogSettingsFragment().show((activity.getFragmentManager()), GHOST_PREFS_TAG);
            }
        });
        if(ghostEnabled.get()) toggle.setChecked(true);

        return layout;
    }
}
