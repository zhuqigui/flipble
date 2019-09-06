package com.android.flipble.logcat;

import android.util.Log;

public class Slog {

    public static boolean DEBUG_V = true;

    public static boolean DEBUG_D = true;

    public static boolean DEBUG_I = true;

    public static boolean DEBUG_E = true;

    public static void v(String TAG, String content) {
        if (DEBUG_V) {
            Log.d(TAG, content);
        }
    }

    public static void d(String TAG, String content) {
        if (DEBUG_D) {
            Log.d(TAG, content);
        }
    }

    public static void i(String TAG, String content) {
        if (DEBUG_I) {
            Log.d(TAG, content);
        }
    }

    public static void e(String TAG, String content) {
        if (DEBUG_E) {
            Log.d(TAG, content);
        }
    }

    public static void i(String content) {
        if (DEBUG_I) {
            Log.d("liu", content);
        }
    }

    public static void e(String content) {
        if (DEBUG_E) {
            Log.d("liu", content);
        }
    }

    public static void v(String content) {
        if (DEBUG_V) {
            Log.d("liu", content);
        }
    }

    public static void d(String content) {
        if (DEBUG_D) {
            Log.d("liu", content);
        }
    }
}
