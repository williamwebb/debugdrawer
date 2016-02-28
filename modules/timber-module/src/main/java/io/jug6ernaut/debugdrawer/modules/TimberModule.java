package io.jug6ernaut.debugdrawer.modules;

import android.app.Activity;
import android.view.View;
import com.jug6ernaut.debugdrawer.views.ButtonElement;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import io.jug6ernaut.debugdrawer.modules.data.LumberYard;
import io.jug6ernaut.debugdrawer.modules.ui.LogDialog;
import timber.log.Timber;

public class TimberModule extends ButtonElement {

    public TimberModule() {
        super("Timber");
    }

    @Override
    protected void onModuleAttached(Activity activity, DebugModule module){
        Timber.plant(LumberYard.getInstance(activity).tree());
    }

    @Override
    public void onClick(View v) {
        new LogDialog(v.getContext()).show();
    }
}
