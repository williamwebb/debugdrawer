package com.jug6ernaut.debugdrawer;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.*;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public abstract class DialogPreferenceFragment extends DialogFragment {

    private PreferenceManager mPreferenceManager;

    /**
     * The starting request code given out to preference framework.
     */
    private static final int FIRST_REQUEST_CODE = 100;

    private static final int MSG_BIND_PREFERENCES = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MSG_BIND_PREFERENCES:
                    bindPreferences();
                    break;
            }
        }
    };
    private ListView lv;
    private int xmlId;

    public DialogPreferenceFragment(int xmlId) {
        this.xmlId = xmlId;
    }

    //must be provided
    public DialogPreferenceFragment() {
        this.xmlId = -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle b) {
        postBindPreferences();
        return lv;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewParent p = lv.getParent();
        if (p != null)
            ((ViewGroup) p).removeView(lv);
    }

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        this.setHasOptionsMenu(true);

        if (b != null)
            xmlId = b.getInt("xml");
        mPreferenceManager = onCreatePreferenceManager();
        lv = new ListView(getActivity());
        lv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (xmlId != -1)
            addPreferencesFromResource(xmlId);
        else addPreferencesFromResource();
        postBindPreferences();
        //((OnPreferenceAttachedListener)getActivity()).onPreferenceAttached(getPreferenceScreen(), xmlId);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityStop");
            m.setAccessible(true);
            m.invoke(mPreferenceManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lv = null;
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityDestroy");
            m.setAccessible(true);
            m.invoke(mPreferenceManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putInt("xml", xmlId);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("dispatchActivityResult", int.class, int.class, Intent.class);
            m.setAccessible(true);
            m.invoke(mPreferenceManager, requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Posts a message to bind the preferences to the list view.
     * <p/>
     * Binding late is preferred as any custom preference types created in
     * {@link #onCreate(Bundle)} are able to have their views recycled.
     */
    private void postBindPreferences() {
        if (mHandler.hasMessages(MSG_BIND_PREFERENCES)) return;
        mHandler.obtainMessage(MSG_BIND_PREFERENCES).sendToTarget();
    }

    private void bindPreferences() {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.bind(lv);
        }
    }

    /**
     * Creates the {@link PreferenceManager}.
     *
     * @return The {@link PreferenceManager} used by this activity.
     */
    private PreferenceManager onCreatePreferenceManager() {
        try {
            Constructor<PreferenceManager> c = PreferenceManager.class.getDeclaredConstructor(Activity.class, int.class);
            c.setAccessible(true);
            PreferenceManager preferenceManager = c.newInstance(this.getActivity(), FIRST_REQUEST_CODE);
            return preferenceManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the {@link PreferenceManager} used by this activity.
     *
     * @return The {@link PreferenceManager}.
     */
    public PreferenceManager getPreferenceManager() {
        return mPreferenceManager;
    }

    /**
     * Sets the root of the preference hierarchy that this activity is showing.
     *
     * @param preferenceScreen The root {@link PreferenceScreen} of the preference hierarchy.
     */
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("setPreferences", PreferenceScreen.class);
            m.setAccessible(true);
            boolean result = (Boolean) m.invoke(mPreferenceManager, preferenceScreen);
            if (result && preferenceScreen != null) {
                postBindPreferences();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the root of the preference hierarchy that this activity is showing.
     *
     * @return The {@link PreferenceScreen} that is the root of the preference
     *         hierarchy.
     */
    public PreferenceScreen getPreferenceScreen() {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("getPreferenceScreen");
            m.setAccessible(true);
            return (PreferenceScreen) m.invoke(mPreferenceManager);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds preferences from activities that match the given {@link Intent}.
     *
     * @param intent The {@link Intent} to query activities.
     */
    public void addPreferencesFromIntent(Intent intent) {
        throw new RuntimeException("too lazy to include this bs");
    }

    /**
     * Inflates the given XML resource and adds the preference hierarchy to the current
     * preference hierarchy.
     *
     * @param preferencesResId The XML resource ID to inflate.
     */
    public void addPreferencesFromResource(int preferencesResId) {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("inflateFromResource", Context.class, int.class, PreferenceScreen.class);
            m.setAccessible(true);
            PreferenceScreen prefScreen = (PreferenceScreen) m.invoke(mPreferenceManager, getActivity(), preferencesResId, getPreferenceScreen());
            setPreferenceScreen(prefScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPreferencesFromResource() {
        try {
            Method m = PreferenceManager.class.getDeclaredMethod("inflateFromResource", Context.class, int.class, PreferenceScreen.class);
            m.setAccessible(true);
            PreferenceScreen prefScreen = createPreferenceHierarchy();
            setPreferenceScreen(prefScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds a {@link Preference} based on its key.
     *
     * @param key The key of the preference to retrieve.
     * @return The {@link Preference} with the key, or null.
     * @see PreferenceGroup#findPreference(CharSequence)
     */
    public Preference findPreference(CharSequence key) {
        if (mPreferenceManager == null) {
            return null;
        }
        return mPreferenceManager.findPreference(key);
    }

    public interface OnPreferenceAttachedListener {
        public void onPreferenceAttached(PreferenceScreen root, int xmlId);
    }

    //    private SharedPreferences prefs = null;
//    private SharedPreferences.Editor edit = null;
    private Context mCtx = null;

    private PreferenceScreen createPreferenceHierarchy() {

        mCtx = getActivity();

        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(mCtx);
//        getPreferenceManager().setSharedPreferencesName(PreferenceManager.getDefaultSharedPreferences(mCtx));

        populatePreferenceScreen(root);

        return root;
    }

    public abstract void populatePreferenceScreen(PreferenceScreen preferenceScreen);

    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    protected static final boolean ALWAYS_SIMPLE_PREFS = false;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    protected static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                // Set the summary to reflect the new value.
                preference
                        .setSummary(index >= 0 ? listPreference.getEntries()[index]
                                : null);
            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    protected static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
                preference,
                preference.getSharedPreferences().getString(preference.getKey(), ""));
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    protected static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link android.preference.PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    protected static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS || !isXLargeTablet(context);
    }
}