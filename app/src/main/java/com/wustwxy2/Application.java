package com.wustwxy2;


import android.content.Context;

import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.wustwxy2.broadcast.DaemonReceiver;
import com.wustwxy2.broadcast.RemindReceiver;
import com.wustwxy2.service.DaemonService;
import com.wustwxy2.service.RemindService;


/**
 * Created by fubicheng on 2016/7/16.
 */
public final class Application extends DaemonApplication{

    @Override
    public void onCreate() {
        super.onCreate();

        //FontsOverride.setDefaultFont(this, "SERIF", "HanSansCN-Normal.ttf");
    }

    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.wustwxy2:process1",
                RemindService.class.getCanonicalName(),
                RemindReceiver.class.getCanonicalName()
        );

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.wustwxy2:process2",
                DaemonService.class.getCanonicalName(),
                DaemonReceiver.class.getCanonicalName()
        );

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();

        return new DaemonConfigurations(configuration1, configuration2, listener);
    }

class MyDaemonListener implements DaemonConfigurations.DaemonListener{
    @Override
    public void onPersistentStart(Context context) {
    }

    @Override
    public void onDaemonAssistantStart(Context context) {
    }

    @Override
    public void onWatchDaemonDaed() {
    }
}
}
