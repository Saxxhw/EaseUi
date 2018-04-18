package com.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.view.View
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.saxxhw.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import com.baidu.speech.VoiceRecognitionService
import android.content.ComponentName
import android.speech.RecognitionListener
import android.content.Intent
import com.iflytek.cloud.resource.Resource.setText


class Main3Activity : BaseActivity(), View.OnTouchListener, RecognitionListener {


    // 离在线语音合成对象
    private lateinit var speechRecognizer: SpeechRecognizer

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
        // 创建识别器
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, ComponentName(this, VoiceRecognitionService::class.java))

    }

    override fun bindListener() {
        btn.setOnTouchListener(this)
        // 注册监听器
        speechRecognizer.setRecognitionListener(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        voiceRecorder.onPressToSpeakBtnTouch(v, event) { voiceFilePath, voiceTimeLength ->
            voice2text(voiceFilePath)
        }
        return false
    }

    /**
     * 语音转文字
     */
    private fun voice2text(voiceFilePath: String) {
        val intent = Intent()
        intent.putExtra("sample", 16000) // 离线仅支持16000采样率
        intent.putExtra("language", "cmn-Hans-CN") // 离线仅支持中文普通话
        intent.putExtra("prop", 20000) // 输入
        intent.putExtra("nlu", "enable") // 语义解析设置
        intent.putExtra("vad", "input") // 语音活动检测-输入模式，适合短信、微博内容等长句输入
        intent.putExtra("infile", voiceFilePath) // 音频源
        speechRecognizer.startListening(intent)
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        println()
    }

    override fun onRmsChanged(p0: Float) {
        println()
    }

    override fun onBufferReceived(p0: ByteArray?) {
        println()
    }

    override fun onPartialResults(p0: Bundle?) {
        println()
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        println()
    }

    override fun onBeginningOfSpeech() {

    }

    override fun onEndOfSpeech() {

    }

    override fun onError(error: Int) {
        val sb = StringBuilder()
        when (error) {
            SpeechRecognizer.ERROR_AUDIO -> sb.append("音频问题")
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> sb.append("没有语音输入")
            SpeechRecognizer.ERROR_CLIENT -> sb.append("其它客户端错误")
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> sb.append("权限不足")
            SpeechRecognizer.ERROR_NETWORK -> sb.append("网络问题")
            SpeechRecognizer.ERROR_NO_MATCH -> sb.append("没有匹配的识别结果")
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> sb.append("引擎忙")
            SpeechRecognizer.ERROR_SERVER -> sb.append("服务端错误")
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> sb.append("连接超时")
        }
    }

    override fun onResults(results: Bundle?) {
        val nbest = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (nbest != null && nbest.size > 0) {
            var result: String? = nbest.get(0)
            if (null != result && result.isNotEmpty()) {
                result = result.replace("，", "")

            }
        }
    }
}
