package com.sfyc.countdownlist.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Author :leilei on 2017/3/27 1452.
 */

public class PrefUtils {
    private static final String STARTED_TIME_ID = "pref_time";
    private SharedPreferences mPreferences;

    public PrefUtils(Context c) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(c);
    }

    public long getStartedTime() {
        return mPreferences.getLong(STARTED_TIME_ID, 0);
    }

    public void setStartedTime(long started) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(STARTED_TIME_ID, started);
        editor.apply();
    }

}
