package com.yida.handset;

import android.util.Log;

/**
 * Created by gujiao on 15-8-19.
 */
public class LogWrapper {

    private static final boolean DEBUGGABLE = true;
    private static final String TAG = "===com.yida.handset===";

    public static void d(String msg) {
        if (DEBUGGABLE) {
            Log.d(TAG, msg);
        }
    }

    public static  void v(String msg) {
        if (DEBUGGABLE) {
            Log.v(TAG, msg);
        }
    }

    public static  void e(String msg) {
        if (DEBUGGABLE) {
            Log.e(TAG, msg);
        }
    }
}
