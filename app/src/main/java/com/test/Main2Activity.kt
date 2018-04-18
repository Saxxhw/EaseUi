package com.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.iflytek.cloud.*
import com.saxxhw.base.BaseActivity
import com.test.runtimepermissions.AudioDecode
import com.test.runtimepermissions.Utils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.io.*


class Main2Activity : BaseActivity(), View.OnTouchListener, RecognizerListener {

    // 语音听写对象
    private lateinit var mIat: SpeechRecognizer
    // 用HashMap存储听写结果
    private val mIatResults = LinkedHashMap<String, String>()

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

        //1、创建SpeechRecognizer对象，第二个参数：本地识别时传InitListener
        mIat = SpeechRecognizer.createRecognizer(this, object :InitListener{
            override fun onInit(p0: Int) {
                println(p0)
            }

        })
        setParam()
    }

    override fun bindListener() {
        btn.setOnTouchListener(this)
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
        mIat.setParameter(SpeechConstant.SAMPLE_RATE, "16000");//设置正确的采样率
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        mIat.setParameter(SpeechConstant.ASR_SOURCE_PATH, voiceFilePath);
        val ret = mIat.startListening(this)
        if (ret != ErrorCode.SUCCESS) {
            toast("识别失败,错误码：" + ret);
        } else {
            val audioData = Utils.File2byte(voiceFilePath)
            if (null != audioData) {
                mIat.writeAudio(audioData, 0, audioData.size)
                mIat.stopListening()
            } else {
                mIat.cancel()
            }
        }
    }

    /**
     * 参数设置
     */
    private fun setParam() {
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        /**
         * 在听写和语音语义理解时，可通过设置此参数，选择要使用的语言区域
         * 当前支持：
         * 简体中文：zh_cn（默认）
         * 美式英文：en_us
         */
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
        /**
         * 每一种语言区域，一般还有不同的方言，通过此参数，在听写和语音语义理解时， 设置不同的方言参数。
         * 当前仅在LANGUAGE为简体中文时，支持方言选择，其他语言区域时， 请把此参数值设为null。
         * 普通话：mandarin(默认)
         * 粤 语：cantonese
         * 四川话：lmz
         * 河南话：henanese
         */
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin")
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
        //设置语音前端点：静音超时时间，即用户多长时间不说话则当做超时处理
        //默认值：短信转写5000，其他4000
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000")
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000")
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1")
        // 设置音频保存路径，保存音频格式支持pcm、wav
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav")
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
        //文本，编码
        mIat.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8")
    }

    override fun onVolumeChanged(volume: Int, p1: ByteArray?) {
        println("当前正在说话，音量大小：$volume")
    }

    override fun onResult(recognizerResult: RecognizerResult?, p1: Boolean) {
        println(recognizerResult)
    }

    override fun onBeginOfSpeech() {
        println()
    }

    override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
        println(p0)
    }

    override fun onEndOfSpeech() {
        println()
    }

    override fun onError(p0: SpeechError?) {
        println(p0)
    }
}
