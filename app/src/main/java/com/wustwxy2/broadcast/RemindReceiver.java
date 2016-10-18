package com.wustwxy2.broadcast;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.wustwxy2.R;
import com.wustwxy2.activity.SearchTableActivity;
import com.wustwxy2.bean.Course;
import com.wustwxy2.models.JwInfoDB;
import com.wustwxy2.service.RemindService;
import com.wustwxy2.util.Utility;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.wustwxy2.activity.LockActivity.newIntent;

/**
 * Created by LvQingYang
 * on 2016/10/14.
 */

public class RemindReceiver extends BroadcastReceiver {
    private static final String TAG = "RemindReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int current=context.getSharedPreferences("data", MODE_PRIVATE).getInt("currentZc",Integer.MAX_VALUE);
        int week=Utility.getWeekOfDate();
        int which=Utility.getClassStart();
        Log.d(TAG, "onReceive: "+current+"  "+week+"  "+which);
        Course course= JwInfoDB.getJwInfoDB(context).loadCurCourse(current,week,which);

        if (course != null) {
            Log.d(TAG, "onReceive: ");
            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (km.inKeyguardRestrictedInputMode()) {
                Intent intent1= newIntent(context,course);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent1);
            }else{
                Intent intent1=new Intent(context, SearchTableActivity.class);
                PendingIntent pi=PendingIntent.getActivity(context,0,intent1,PendingIntent.FLAG_CANCEL_CURRENT);
                NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                Notification notification = builder
                        .setContentTitle(context.getResources().getString(R.string.course_remind))
                        .setContentText("名称: "+course.getName()+"    教室: "+course.getRoom())
                        .setContentIntent(pi)
                        .setColor(Color.parseColor("#06c1ae"))
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.table)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                context.getResources(), R.mipmap.ic_launcher))
                        .build();
                manager.notify(1, notification);
            }
        }
        Intent i=new Intent(context, RemindService.class);
        context.startService(i);
    }
}
