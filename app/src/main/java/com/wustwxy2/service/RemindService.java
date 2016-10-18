package com.wustwxy2.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.wustwxy2.broadcast.RemindReceiver;
import com.wustwxy2.util.Utility;

/**
 * Created by LvQingYang
 * on 2016/10/14.
 */

public class RemindService extends Service {

    private static final String TAG = "RemindService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime()+ Utility.getTimeCode()*60*1000;
        Intent i=new Intent(this, RemindReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this, 0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }
}
