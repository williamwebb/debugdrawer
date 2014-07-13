package com.jug6ernaut.debugdrawer.views.groups;

import android.app.Activity;
import com.jug6ernaut.debugdrawer.views.DebugGroup;
import com.jug6ernaut.debugdrawer.views.TextElement;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by williamwebb on 7/2/14.
 */
public class BuildGroup extends DebugGroup {
    private static final DateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    public BuildGroup(Activity activity){
        this(activity,activity.getPackageName());
    }

    public BuildGroup(Activity activity, String packageName) {
        super("Build Information", activity);

        String buildConfigPackage = packageName+".BuildConfig";

        addElement(new TextElement(activity, "Name:", getViaReflection(String.class,buildConfigPackage,"VERSION_NAME")));
        addElement(new TextElement(activity, "Code:", getViaReflection(Integer.class,buildConfigPackage,"VERSION_CODE")+""));
        addElement(new TextElement(activity, "SHA:", getViaReflection(String.class,buildConfigPackage,"GIT_SHA")));

        String buildTime = "";
        try {
            String buildTimeString = getViaReflection(String.class,buildConfigPackage,"BUILD_TIME");

            // Parse ISO8601-format time into local time.
            DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            inFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            buildTime = DATE_DISPLAY_FORMAT.format(inFormat.parseObject(buildTimeString));
        } catch (Exception e) {
            e.printStackTrace();
        }
        addElement(new TextElement(activity, "Date:", buildTime));
    }

    private <T> T getViaReflection(Class<T> type, String className, String fieldName){
        try {
            Class tClass = Class.forName(className);

            Field tField = tClass.getDeclaredField(fieldName);
            tField.setAccessible(true);
            return type.cast(tField.get(null));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
