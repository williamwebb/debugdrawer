package com.jug6ernaut.debugdrawer.views.modules;

import android.app.Activity;
import com.jug6ernaut.debugdrawer.DebugView;
import com.jug6ernaut.debugdrawer.views.DebugModule;
import com.jug6ernaut.debugdrawer.views.TextElement;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by williamwebb on 7/2/14.
 */
public class BuildModule extends DebugModule {
    private static final DateFormat DATE_DISPLAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    public BuildModule() {
        super("Build Information");
    }

    @Override
    protected void onAttach(Activity activity, DebugView parent) {
        String packageName = activity.getPackageName();
        String buildConfigPackage = packageName+".BuildConfig";

        addElement(new TextElement("Name:", getViaReflection(String.class,buildConfigPackage,"VERSION_NAME")));
        addElement(new TextElement("Code:", getViaReflection(Integer.class,buildConfigPackage,"VERSION_CODE")+""));
        addElement(new TextElement("SHA:", getViaReflection(String.class,buildConfigPackage,"GIT_SHA")));

        try {
            String buildTimeString = getViaReflection(String.class,buildConfigPackage,"BUILD_TIME");

            // Parse ISO8601-format time into local time.
            DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            inFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String buildTime = DATE_DISPLAY_FORMAT.format(inFormat.parseObject(buildTimeString));
            addElement(new TextElement("Date:", buildTime));

        } catch (Exception e) { }
    }

    private <T> T getViaReflection(Class<T> type, String className, String fieldName){
        try {
            Class tClass = Class.forName(className);

            Field tField = tClass.getDeclaredField(fieldName);
            tField.setAccessible(true);
            return type.cast(tField.get(null));
        }catch (Exception e){
            return null;
        }
    }

}
