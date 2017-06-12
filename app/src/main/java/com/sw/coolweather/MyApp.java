package com.sw.coolweather;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by xifanoo on 2017/6/9.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
