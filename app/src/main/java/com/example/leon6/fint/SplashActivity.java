package com.example.leon6.fint;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class SplashActivity extends Activity {

    Intent intent = new Intent(this, SuccessActivity.class);
    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Handler hd = new Handler();

        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback = new SplashActivity.SessionCallback();
                Session.getCurrentSession().addCallback(callback);
                Session.getCurrentSession().checkAndImplicitOpen();
            }
        }, 2000);

    }

    // 액티비티 전환 시 필요
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {
            gotomap();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
        }
    }
    public void gotomap(){

        Intent intent = new Intent(this, SuccessActivity.class);
        startActivity(intent);
        finish();
    }
}
