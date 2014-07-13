package com.jug6ernaut.debugdrawer.views.groups;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import com.jug6ernaut.debugdrawer.Strings;
import com.jug6ernaut.debugdrawer.views.DebugGroup;
import com.jug6ernaut.debugdrawer.views.TextElement;

/**
 * Created by williamwebb on 7/2/14.
 */
public class DeviceInfoGroup extends DebugGroup {
    public DeviceInfoGroup(Activity activity) {
        super("Device Information", activity);

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        String densityBucket = getDensityString(dm);

        addElement(new TextElement(activity, "Make", Strings.truncateAt(Build.MANUFACTURER, 20)));
        addElement(new TextElement(activity, "Model", Strings.truncateAt(Build.MODEL, 20)));
        addElement(new TextElement(activity, "Resolution", dm.heightPixels + "x" + dm.widthPixels));
        addElement(new TextElement(activity, "Density", dm.densityDpi + "dpi (" + densityBucket + ")"));
        addElement(new TextElement(activity, "Release", Build.VERSION.RELEASE));
        addElement(new TextElement(activity, "API", String.valueOf(Build.VERSION.SDK_INT)));
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
