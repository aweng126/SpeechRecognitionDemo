package com.example.kingwen.speechrecognitiondemo.config;

import android.app.Application;
import android.util.Log;

import com.iflytek.cloud.SpeechUtility;

/**
 * Created by kingwen on 2016/3/30.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        SpeechUtility.createUtility(MyApplication.this, "appid=" + "56f7edc7");
        Log.e("creat","hello");
        super.onCreate();
    }
}
