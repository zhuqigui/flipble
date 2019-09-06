//=============================================================================
//
// Copyright 2016 Ximmerse, LTD. All rights reserved.
//
//=============================================================================

package com.android.flipble.util;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.app.Activity;

public class Jugger {

    protected static final int S_TO_MS = 1000;

    public static class DelayedCall {

        public Runnable call;
        public long time;

        public DelayedCall(Runnable call, long delay) {
            this.call = call;
            this.time = delay;
        }
    }

    public Context context;
    public int deltaTime;
    protected Timer mTimer;
    protected ArrayList<DelayedCall> mDelayedCalls = new ArrayList<DelayedCall>();

    public Jugger(Context context) {
        this.context = context;
        this.deltaTime = 1000;// 20fps. 50
    }

    protected DelayedCall popDelayedCall(Runnable call, long delay) {
        return new DelayedCall(call, delay);
    }

    protected void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer("Juggler Timer");
            //
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (mDelayedCalls) {
                        DelayedCall dc = null;
                        for (int i = 0, imax = mDelayedCalls.size(); i < imax; ++i) {
                            dc = mDelayedCalls.get(i);
                            //
                            dc.time -= deltaTime;
                            if (dc.time <= 0) {
                                if (dc.call != null) {
                                    if (context instanceof Activity) {
                                        ((Activity) context).runOnUiThread(dc.call);
                                    } else {
                                        dc.call.run();
                                    }
                                }
                                // Remove timeout one.
                                mDelayedCalls.remove(i);
                                --i;
                                --imax;
                            }
                        }
                    }
                    // Stop the timer if no DelayedCall.
                    if (mDelayedCalls.size() <= 0) {
                        stopTimer();
                    }
                }

            }, deltaTime, deltaTime);
        }
    }

    protected void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            //
            mTimer = null;
        }
    }

    public void delayCall(Runnable call, long delay) {
        synchronized (mDelayedCalls) {
            mDelayedCalls.add(popDelayedCall(call, delay));
            startTimer();
        }
    }

    public void clearAll() {
        synchronized (mDelayedCalls) {
            mDelayedCalls.clear();
            stopTimer();
        }
    }

}