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

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.Time;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import com.jug6ernaut.debugdrawer.R;
import com.readystatesoftware.ghostlog.integration.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class ServerlessLogScreen implements
        SharedPreferences.OnSharedPreferenceChangeListener, LogReceiver.Callbacks {

    public static final String ACTION_ROOT_FAILED = "com.readystatesoftware.ghostlog.ROOT_FAILED";

    private static final String TAG = "LogService";
    private static final int LOG_BUFFER_LIMIT = 2000;
    private static final SimpleDateFormat LOGCAT_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

    private boolean sIsRunning = false;
    private boolean sWasRunning = false;

    private boolean mIntegrationEnabled = false;
    private boolean mIsLogPaused = false;
    private String mLogLevel;
    private boolean mAutoFilter;
    private int mForegroundAppPid;
    private String mTagFilter;
    private SharedPreferences mPrefs;
    private ListView mLogListView;
    private LogAdapter mAdapter;
    private LinkedList<LogLine> mLogBuffer;
    private LinkedList<LogLine> mLogBufferFiltered;

    private Handler mLogBufferUpdateHandler = new Handler();
    private LogReaderAsyncTask mLogReaderTask;
    private ProcessMonitorAsyncTask mProcessMonitorTask;
    private Activity mContext;

    private int viewWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    private int viewHeight = ViewGroup.LayoutParams.MATCH_PARENT;
    private int viewGravity = 0;

    public boolean isRunning() {
        return sIsRunning;
    }

    public ServerlessLogScreen(Activity activity, int width, int height, int gravity) {
        this(activity);
        this.viewWidth = width;
        this.viewHeight = height;
        this.viewGravity = gravity;
    }

    public ServerlessLogScreen(Activity activity){
        mContext = activity;
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mPrefs.registerOnSharedPreferenceChangeListener(this);
        mLogLevel = mPrefs.getString(getString(R.string.pref_log_level), LogLine.LEVEL_VERBOSE);
        mAutoFilter = mPrefs.getBoolean(getString(R.string.pref_auto_filter), false);
        mTagFilter = mPrefs.getString(getString(R.string.pref_tag_filter), null);

        mLogListView = (ListView) LayoutInflater.from(mContext).inflate(R.layout.window_log, null);

        mContext.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}
            @Override public void onActivityStopped(Activity activity) { }
            @Override public void onActivityStarted(Activity activity) { }
            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

            @Override
            public void onActivityResumed(Activity activity) {
                System.out.println("onActivityResumed:" + activity);
                if (sWasRunning) {
                    start();
                    sWasRunning = false;
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                System.out.println("onActivityPaused:" + activity);
                if (sIsRunning) {
                    stop(activity);
                    sWasRunning = true;
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activity.getApplication().unregisterActivityLifecycleCallbacks(this);
            }
        });
    }

    public void start(){
        System.out.println("start(). Running: "  + isRunning());
        if(isRunning())return;

        sIsRunning = true;
        createSystemWindow(mContext);
        startLogReader();
        if (mAutoFilter) {
            startProcessMonitor();
        }
    }
    public void stop(final Activity activity){
        System.out.println("stop(). Running: "  + isRunning());
        if(!isRunning())return;

        sIsRunning = false;
        removeSystemWindow(activity,mLogListView);
        mPrefs.unregisterOnSharedPreferenceChangeListener(ServerlessLogScreen.this);
        stopLogReader();
        stopProcessMonitor();
    }

    public void stop() {
        stop(mContext);
    }

    private void createSystemWindow(Activity activity) {
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
            viewWidth,
            viewHeight,
            0,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        );
        lp.gravity = viewGravity;

        setSystemViewBackground(mPrefs.getInt(getString(R.string.pref_bg_opacity), 0));
        mLogBuffer = new LinkedList<LogLine>();
        mLogBufferFiltered = new LinkedList<LogLine>();
        mAdapter = new LogAdapter(mContext, mLogBufferFiltered,TAG);
        mLogListView.setAdapter(mAdapter);
        activity.getWindowManager().addView(mLogListView, lp);
    }

    private void removeSystemWindow(Activity activity, ListView listView) {
        if (listView != null && listView.getParent() != null) {
            final WindowManager wm = activity.getWindowManager();
            wm.removeViewImmediate(listView);
        }
    }

    private void sendIntegrationBroadcast(boolean enable) {
        Intent intent = new Intent(Constants.ACTION_COMMAND);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constants.EXTRA_ENABLED, enable);
        intent.putExtras(bundle);
        mContext.sendBroadcast(intent);
    }

    private void startLogReader() {
        mLogBuffer = new LinkedList<LogLine>();
        mLogBufferFiltered = new LinkedList<LogLine>();
        mLogReaderTask = new LogReaderAsyncTask() {
            @Override
            protected void onProgressUpdate(LogLine... values) {
                // process the latest logcat lines
                for (LogLine line : values) {
                    updateBuffer(line);
                }
            }
            @Override
            protected void onPostExecute(Boolean ok) {
                if (!ok) {
                    // not root - notify activity
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(new Intent(ACTION_ROOT_FAILED));
                    // setEnabled integration
                    mIntegrationEnabled = true;
                    sendIntegrationBroadcast(true);
                    updateBuffer(new LogLine("0 " + LOGCAT_TIME_FORMAT.format(new Date())
                            + " 0 0 " + getString(R.string.canned_integration_log_line)));
                }
            }
        };
        mLogReaderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.i(TAG, "Log reader task started");
    }

    private void stopLogReader() {
        if (mLogReaderTask != null) {
            mLogReaderTask.cancel(true);
        }
        mLogReaderTask = null;
        Log.i(TAG, "Log reader task stopped");
    }

    private void startProcessMonitor() {
        mProcessMonitorTask = new ProcessMonitorAsyncTask(mContext) {
            @Override
            protected void onProgressUpdate(ForegroundProcessInfo... values) {
                mForegroundAppPid = values[0].pid;
                updateBuffer();
            }
        };
        mProcessMonitorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.i(TAG, "process monitor task started");
    }

    private void stopProcessMonitor() {
        if (mProcessMonitorTask != null) {
            mProcessMonitorTask.cancel(true);
        }
        mProcessMonitorTask = null;
        Log.i(TAG, "process monitor task stopped");
    }

    private void updateBuffer() {
        updateBuffer(null);
    }

    private void updateBuffer(final LogLine line) {
        mLogBufferUpdateHandler.post( new Runnable() {
            @Override
            public void run() {

                // update raw buffer
                if (line != null && line.getLevel() != null) {
                    mLogBuffer.add(line);
                }

                // update filtered buffer
                mLogBufferFiltered.clear();
                for (LogLine bufferedLine : mLogBuffer) {
                    if (!isFiltered(bufferedLine)) {
                        mLogBufferFiltered.add(bufferedLine);
                    }
                }

                // update adapter
                if (!mIsLogPaused) {
                    mAdapter.setData(mLogBufferFiltered);
                }

                // purge old entries
                while(mLogBuffer.size() > LOG_BUFFER_LIMIT) {
                    mLogBuffer.remove();
                }

            }
        });
    }

    private boolean isFiltered(LogLine line) {
        if (line != null) {
            if (mAutoFilter && mForegroundAppPid != 0) {
                if (line.getPid() != mForegroundAppPid) {
                    return true;
                }
            }
            if (!LogLine.LEVEL_VERBOSE.equals(mLogLevel)) {
                if (line.getLevel() != null && !line.getLevel().equals(mLogLevel)) {
                    return true;
                }
            }
            if (mTagFilter != null) {
                if (line.getTag() == null || !line.getTag().toLowerCase().contains(mTagFilter.toLowerCase())) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    private void setSystemViewBackground(int v) {
        int level = 0;
        if (v > 0) {
            int a = (int) ((float)v/100f * 255);
            mLogListView.setBackgroundColor(Color.argb(a, level, level, level));
        } else {
            mLogListView.setBackgroundDrawable(null);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG,key);
        if(mAdapter == null) return;
        mAdapter.updateAppearance();

        if (key.equals(getString(R.string.pref_bg_opacity))) {
            setSystemViewBackground(mPrefs.getInt(getString(R.string.pref_bg_opacity), 0));
        } else if (key.equals(getString(R.string.pref_log_level))) {
            mLogLevel = mPrefs.getString(getString(R.string.pref_log_level), LogLine.LEVEL_VERBOSE);
            updateBuffer();
        } else if (key.equals(getString(R.string.pref_auto_filter))) {
            mAutoFilter = mPrefs.getBoolean(getString(R.string.pref_auto_filter), false);
            if (mAutoFilter) {
                startProcessMonitor();
            } else {
                stopProcessMonitor();
            }
            updateBuffer();
        } else if (key.equals(getString(R.string.pref_tag_filter))) {
            mTagFilter = mPrefs.getString(getString(R.string.pref_tag_filter), null);
            updateBuffer();
        }

        System.out.println(
                "TS: " + mPrefs.getInt(mContext.getString(R.string.pref_text_size), 0) +
                "TO: " + mPrefs.getInt(mContext.getString(R.string.pref_text_opacity), 0) +
                "BO: " + mPrefs.getInt(getString(R.string.pref_bg_opacity), 0));
    }

    @Override
    public void onLogPause() {
        mIsLogPaused = true;
    }

    @Override
    public void onLogResume() {
        mIsLogPaused = false;
        updateBuffer();
    }

    @Override
    public void onLogClear() {
        mLogBuffer = new LinkedList<LogLine>();
        updateBuffer();
    }

    @Override
    public void onLogShare() {
        StringBuffer sb = new StringBuffer();
        for (LogLine line : mLogBufferFiltered) {
            sb.append(line.getRaw());
            sb.append("\n");
        }
        Time now = new Time();
        now.setToNow();
        String ts = now.format3339(false);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject) + " " + ts);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(shareIntent);
    }

    @Override
    public void onIntegrationDataReceived(String line) {
        if (mIntegrationEnabled) {
            updateBuffer(new LogLine(line));
        }
    }

    private String getString(int res){
        return mContext.getString(res);
    }

}

