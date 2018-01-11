package com.test

import android.os.Bundle
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.saxxhw.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun getLayout(): Int = R.layout.activity_main

    override fun initEventAndData(savedInstanceState: Bundle?) {

        EMClient.getInstance().login("KM4", "abcd9882", object : EMCallBack {
            override fun onSuccess() {
                println("登录成功！！！！")
            }

            override fun onProgress(p0: Int, p1: String?) {
                println("登录失败！！！！$p1")
            }

            override fun onError(p0: Int, p1: String?) {

            }
        })
    }
}
