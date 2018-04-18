package com.test;

import android.app.Application;

import com.hyphenate.easeui.init.EaseInitHelper;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by Saxxhw on 2018/3/22.
 * 邮箱：Saxxhw@126.com
 * 功能：
 */

public class Applicant extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EaseInitHelper.getInstance().init(this, MainActivity.class, MainActivity.class);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5abc470d");
    }
}
