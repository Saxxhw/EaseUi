package com.test

import android.content.Intent
import android.os.Bundle
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.easeui.ui.EaseVideoActivity
import com.hyphenate.util.PathUtil
import com.saxxhw.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivityForResult
import com.test.runtimepermissions.PermissionsResultAction
import com.test.runtimepermissions.PermissionsManager



class MainActivity : BaseActivity() {

    override fun getLayout(): Int = R.layout.activity_main

    override fun initEventAndData(savedInstanceState: Bundle?) {
      EMClient.getInstance().login("KM3", "abcd9882", object :EMCallBack{
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
    }

    override fun bindListener() {
        btn.setOnClickListener {
            startActivityForResult<EaseVideoActivity>(200)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 200) {
            val path = data?.getStringExtra(EaseVideoActivity.VIDEO_PATH)
            val duration = data?.getIntExtra(EaseVideoActivity.VIDEO_DURATION, 0)
            text.text = "path=${path}，duration=$duration"
        }
    }
}
