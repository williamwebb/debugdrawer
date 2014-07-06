package com.jug6ernaut.debugdrawer.views.groups;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import com.jug6ernaut.debugdrawer.Strings;
import com.jug6ernaut.debugdrawer.views.DebugGroup;
import com.jug6ernaut.debugdrawer.views.TextElement;

/**
 * Created by williamwebb on 7/2/14.
 */
public class DeviceInfoGroup extends DebugGroup {
    public DeviceInfoGroup(Context context) {
        super("Device Information", context);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        String densityBucket = getDensityString(dm);

        addElement(new TextElement(context, "Make", Strings.truncateAt(Build.MANUFACTURER, 20)));
        addElement(new TextElement(context, "Model", Strings.truncateAt(Build.MODEL, 20)));
        addElement(new TextElement(context, "Resolution", dm.heightPixels + "x" + dm.widthPixels));
        addElement(new TextElement(context, "Density", dm.densityDpi + "dpi (" + densityBucket + ")"));
        addElement(new TextElement(context, "Release", Build.VERSION.RELEASE));
        addElement(new TextElement(context, "API", String.valueOf(Build.VERSION.SDK_INT)));
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
//            case DisplayMetrics.DENSITY_XXHIGH:
//                return "xxhdpi";
//            case DisplayMetrics.DENSITY_XXXHIGH:
//                return "xxxhdpi";
            case DisplayMetrics.DENSITY_TV:
                return "tvdpi";
            default:
                return "unknown";
        }
    }
}
