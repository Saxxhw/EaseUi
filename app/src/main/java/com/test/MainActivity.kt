package com.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.baidu.speech.EventListener
import com.baidu.speech.EventManager
import com.baidu.speech.EventManagerFactory
import com.baidu.speech.asr.SpeechConstant
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.saxxhw.base.BaseActivity
import com.test.runtimepermissions.AudioDecode
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : BaseActivity(), View.OnTouchListener, EventListener {

    // 语音识别
    private lateinit var asr: EventManager

    override fun getLayout(): Int = R.layout.activity_main

    override fun initEventAndData(savedInstanceState: Bundle?) {
        EMClient.getInstance().login("KM3", "abcd9882", object : EMCallBack {
            override fun onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
            }

            override fun onProgress(p0: Int, p1: String?) {

            }

            override fun onError(p0: Int, p1: String?) {
                val s = p1
            }
        })

        // 初始化语音识别
        asr = EventManagerFactory.create(this, "asr")
    }

    override fun bindListener() {
        btn.setOnTouchListener(this)
        asr.registerListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        voiceRecorder.onPressToSpeakBtnTouch(v, event) { voiceFilePath, voiceTimeLength ->
            voice2text(voiceFilePath)
        }
        return false
    }

    override fun onEvent(name: String?, params: String?, data: ByteArray?, offset: Int, length: Int) {
        println(name)
    }

    /**
     * 语音转文字
     */
    private fun voice2text(voiceFilePath: String) {
        val params = mutableMapOf<String, Any>()
        params[SpeechConstant.ACCEPT_AUDIO_VOLUME] = false
        params[SpeechConstant.IN_FILE] = voiceFilePath
        val json = JSONObject(params).toString()
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0)
    }
}
