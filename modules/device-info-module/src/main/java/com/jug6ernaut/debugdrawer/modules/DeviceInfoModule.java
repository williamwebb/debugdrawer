package com.jug6ernaut.debugdrawer.modules;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import com.jug6ernaut.debugdrawer.DebugView;
import com.jug6ernaut.debugdrawer.Strings;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.TextElement;

/**
 * Created by williamwebb on 7/2/14.
 */
public class DeviceInfoModule extends DebugModule {

    public DeviceInfoModule() {
        super("Device Information");
    }

	public DeviceInfoModule(String moduleName) {
		super(moduleName);
	}

    @Override
    protected void onAttach(Activity activity, DebugView parent, ViewGroup content) {
	    DisplayMetrics dm = activity.getResources().getDisplayMetrics();
	    String densityBucket = getDensityString(dm);

	    addElement(new TextElement("Make", Strings.truncateAt(Build.MANUFACTURER, 20)));
	    addElement(new TextElement("Model", Strings.truncateAt(Build.MODEL, 20)));
	    addElement(new TextElement("Resolution", dm.heightPixels + "x" + dm.widthPixels));
	    addElement(new TextElement("Density", dm.densityDpi + "dpi (" + densityBucket + ")"));
	    addElement(new TextElement("Release", Build.VERSION.RELEASE));
	    addElement(new TextElement("API", String.valueOf(Build.VERSION.SDK_INT)));
    }

    private static String getDensityString(DisplayMetrics displayMetrics) {
        switch (displayMetrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                return "ldpi";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "mdpi";
            case DisplayMetrics.DENSITY_HIGH:
                return "hdpi";
            case DisplayMetrics.DENSITY_XHIGH:
                return "xhdpi";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "xxhdpi";
            case DisplayMetrics.DENSITY_XXXHIGH:
                return "xxxhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            default:
                return "unknown";
        }
    }
}
