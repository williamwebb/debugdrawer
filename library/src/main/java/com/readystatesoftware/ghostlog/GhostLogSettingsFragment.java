/*
 * Copyright (C) 2013 readyState Software Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.readystatesoftware.ghostlog;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.*;
import android.support.v4.content.LocalBroadcastManager;
import android.view.*;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import com.jug6ernaut.debugdrawer.DialogPreferenceFragment;
import com.jug6ernaut.debugdrawer.R;

public class GhostLogSettingsFragment extends DialogPreferenceFragment {

    private static final int CODE_TAG_FILTER = 1;
    private static Preference sTagFilterPref;

    private SharedPreferences mPrefs;
    
    private Activity mContext;

    private BroadcastReceiver mRootFailReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            processRootFail();
        }
    };

    public GhostLogSettingsFragment(){
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this.getActivity();

//        Switch mainSwitch = new Switch(mContext);
//        mainSwitch.setChecked(LogService.isRunning());
//        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                Intent intent = new Intent(mContext, LogService.class);
//                if (b) {
//                    if (!LogService.isRunning()) {
//                        mContext.startService(intent);
//                    }
//                } else {
//                    mContext.stopService(intent);
//                }
//            }
//        });
//
//        final ActionBar bar = mContext.getActionBar();
//        final ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
//        lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.main_switch_margin_right);
//        bar.setCustomView(mainSwitch, lp);
//        bar.setDisplayShowCustomEnabled(true);

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (!mPrefs.getBoolean(PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, false)) {
            PreferenceManager.setDefaultValues(mContext, R.xml.pref_filters, true);
            PreferenceManager.setDefaultValues(mContext, R.xml.pref_appearance, true);
            SharedPreferences.Editor edit = mPrefs.edit();
            edit.putBoolean(PreferenceManager.KEY_HAS_SET_DEFAULT_VALUES, true);
            edit.apply();
        }

        setupSimplePreferencesScreen();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter f = new IntentFilter();
//        f.addAction(LogService.ACTION_ROOT_FAILED);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mRootFailReceiver, f);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mRootFailReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_TAG_FILTER) {
            if (resultCode == Activity.RESULT_OK) {
                mPrefs.edit().putString(getString(R.string.pref_tag_filter), data.getAction()).apply();
                sTagFilterPref.setSummary(data.getAction());
            }
        }
    }

    @Override
    public void populatePreferenceScreen(PreferenceScreen preferenceScreen) {

    }

//    @Override
//    public void onBuildHeaders(List<Header> target) {
//        if (!isSimplePreferences(mContext)) {
//            loadHeadersFromResource(R.xml.pref_headers, target);
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sTagFilterPref = null;
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(mContext)) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_blank);

        // Add 'filters' preferences.
        PreferenceCategory fakeHeader = new PreferenceCategory(mContext);
        fakeHeader.setTitle(R.string.filters);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_filters);
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_log_level)));
        setupTagFilterPreference(mContext, findPreference(getString(R.string.pref_tag_filter)));
        sTagFilterPref = findPreference(getString(R.string.pref_tag_filter));

        // Add 'appearance' preferences.
        fakeHeader = new PreferenceCategory(mContext);
        fakeHeader.setTitle(R.string.appearance);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_appearance);

        // Add 'info' preferences.
        fakeHeader = new PreferenceCategory(mContext);
        fakeHeader.setTitle(R.string.information);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_info);
        setupOpenSourceInfoPreference(mContext, findPreference(getString(R.string.pref_info_open_source)));
        setupVersionPref(mContext, findPreference(getString(R.string.pref_version)));

    }

    private void processRootFail() {

        int failCount = mPrefs.getInt(getString(R.string.pref_root_fail_count), 0);
        if (failCount == 0) {
            // show dialog first time
            AlertDialog dlg = new AlertDialog.Builder(mContext)
                    .setTitle(R.string.no_root)
                    .setMessage(R.string.no_root_dialog)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // ok, do nothing
                        }
                    })
                    .setNeutralButton(getString(R.string.github), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.repo_url)));
                            startActivity(intent);
                        }
                    })
                    .create();
            dlg.show();
            mPrefs.edit().putInt(getString(R.string.pref_root_fail_count), failCount+1).apply();
        } else if (failCount <= 3) {
            // show toast 3 more times
            Toast.makeText(mContext, R.string.toast_no_root, Toast.LENGTH_LONG).show();
            mPrefs.edit().putInt(getString(R.string.pref_root_fail_count), failCount+1).apply();
        }

    }

    private static void setupTagFilterPreference(final Activity activity, Preference preference) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
//        preference.setSummary(prefs.getString(activity.getString(R.string.pref_tag_filter), null));
//        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                Intent intent = new Intent(activity, TagFilterListActivity.class);
//                activity.startActivityForResult(intent, CODE_TAG_FILTER);
//                return true;
//            }
//        });
    }

    private static void setupOpenSourceInfoPreference(final Activity activity, Preference preference) {
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager fm = activity.getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                new OpenSourceLicensesDialog().show(ft, "dialog");
                return true;
            }
        });
    }

    private static void setupVersionPref(final Activity activity, Preference preference) {
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                FragmentManager fm = activity.getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                new AboutDialog().show(ft, "dialog");
                return true;
            }
        });
    }

    public static class FilterPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_filters);
            bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_log_level)));
            setupTagFilterPreference(getActivity(), findPreference(getString(R.string.pref_tag_filter)));
            sTagFilterPref = findPreference(getString(R.string.pref_tag_filter));
        }
    }

    public static class AppearancePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_appearance);
        }
    }

    public static class InfoPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.pref_info);
            setupOpenSourceInfoPreference(getActivity(), findPreference(getString(R.string.pref_info_open_source)));
            setupVersionPref(getActivity(), findPreference(getString(R.string.pref_version)));
        }
    }

    public static class OpenSourceLicensesDialog extends DialogFragment {

        public OpenSourceLicensesDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            WebView webView = new WebView(getActivity());
            webView.loadUrl("file:///android_asset/licenses.html");

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.open_source_licences)
                    .setView(webView)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
        }
    }

    public static class AboutDialog extends DialogFragment {

        public AboutDialog() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View content = inflater.inflate(R.layout.dialog_about, null, false);
            TextView version = (TextView) content.findViewById(R.id.version);

            try {
                String name = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
                version.setText(getString(R.string.version) + " " + name);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.app_name)
                    .setView(content)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
        }
    }

    private final class LogActionMode implements ActionMode.Callback {

        private static final int MENU_SEND_LOG = 1;
        private static final int MENU_CLEAR_LOG = 2;
        private static final int MENU_PAUSE_LOG = 3;
        private String SEND_LOG = "Send Logs";
        private String CLEAR_LOG = "Clear Logs";
        private String PAUSE_LOG = "Pause";

        private int previusNavigationMode = -1;

        Fragment f = null;
        ActionBar ab = null;
        public LogActionMode(ActionBar ab, Fragment f){
            this.ab = ab;
            this.f = f;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            getFragmentManager().beginTransaction().remove(f).commit();
            ab.setNavigationMode(previusNavigationMode);
        }
    }
}
