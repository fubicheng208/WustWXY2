package com.wustwxy2.broadcast;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wustwxy2.service.RemindService;


public class DaemonReceiver extends BroadcastReceiver {

    boolean isServiceRunning = false;

    public DaemonReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {


        throw new UnsupportedOperationException("Not yet implemented");
    }
}
