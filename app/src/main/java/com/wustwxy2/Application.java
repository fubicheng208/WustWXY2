package com.wustwxy2;


import com.wustwxy2.models.FontsOverride;

/**
 * Created by fubicheng on 2016/7/16.
 */
public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //FontsOverride.setDefaultFont(this, "SERIF", "HanSansCN-Normal.ttf");
    }
}
