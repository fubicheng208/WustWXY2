package com.wustwxy2.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.wustwxy2.R;
import com.wustwxy2.activity.SearchTableActivity;
import com.wustwxy2.broadcast.RemindReceiver;
import com.wustwxy2.util.Utility;

/**
 * Created by LvQingYang
 * on 2016/10/14.
 */

public class RemindService extends Service {

    private static final String TAG = "RemindService";
    private static final int REMINDING_NOW = 2;
    Notification notification;
    SharedPreferences sp;
    int state;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        state = intent.getIntExtra("state",2);
        Log.i(TAG,"onBind: state"+state);
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "RemindService onCreate!" + state);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent intent = new Intent(this, SearchTableActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        //PendingIntent pi = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent,PendingIntent.FLAG_CANCEL_CURRENT);

        notification = builder
                .setContentTitle("武科大微校园")
                .setContentText("可点击进入课表界面，点击右上角的灰色按钮关闭上课提醒功能")
                .setContentIntent(pi)
                .setColor(Color.parseColor("#06c1ae"))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(
                        getResources(), R.mipmap.ic_launcher))
                .build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "RemindService onStartCommand!");
        /*sp = getSharedPreferences("data",MODE_PRIVATE);
        int state = sp.getInt("remindState",2);
        Log.i(TAG, "StartCommand -state: " + state);*/
        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime()+ Utility.getTimeCode()*60*1000;
        Intent i=new Intent(this, RemindReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this, 0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        startForeground(REMINDING_NOW, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "RemindService onDestroy!");
        /*sp = getSharedPreferences("data",MODE_PRIVATE);
        int state = sp.getInt("remindState",2);*/
        /*Log.i(TAG, "onDestroy--state: " + state);
        if(state != 0){
            Intent i = new Intent(this, RemindReceiver.class);
            sendBroadcast(i);
        }*/
        stopForeground(true);
    }
}
