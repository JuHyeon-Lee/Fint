package com.example.leon6.fint;

import android.app.Activity;
import android.app.Application;

import com.kakao.auth.KakaoSDK;

public class GloabalApplication extends Application {
    private static volatile GloabalApplication obj = null;
    private static volatile Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        obj = this;
        KakaoSDK.init(new KakaoSDKAdapter());
    }

    public static GloabalApplication getGlobalApplicationContext() {
        return obj;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    // Activity가 올라올때마다 Activity의 onCreate에서 호출해줘야한다.
    public static void setCurrentActivity(Activity currentActivity) {
        GloabalApplication.currentActivity = currentActivity;
    }
}