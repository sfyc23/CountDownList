package com.sfyc.countdownlist.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Author :leilei on 2017/3/27 1452.
 */
public class PrefUtils {
    private static final String PREF_NAME = "countdown_prefs";
    private static final String STARTED_TIME_ID = "pref_time";
    private final SharedPreferences mPreferences;

    public PrefUtils(Context c) {
        mPreferences = c.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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
