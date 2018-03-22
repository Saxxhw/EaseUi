package com.hyphenate.easeui.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle

import com.hyphenate.easeui.R
import com.saxxhw.base.BaseActivity

import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.hyphenate.easeui.utils.video.ImageResizer
import org.jetbrains.anko.find
import com.hyphenate.easeui.utils.video.ImageCache
import android.provider.MediaStore
import android.view.ViewTreeObserver
import com.hyphenate.easeui.utils.video.Utils
import android.net.Uri
import com.hyphenate.easeui.adapter.ImageAdapter
import com.hyphenate.easeui.model.VideoEntity

/**
 * Created by Saxxhw on 2018/3/22.
 * 邮箱：Saxxhw@126.com
 * 功能：
 */

class EaseVideoActivity : BaseActivity(), OnItemClickListener, AbsListView.OnScrollListener, ViewTreeObserver.OnGlobalLayoutListener {

    companion object {
        // 公共参数
        val VIDEO_PATH = "path"
        val VIDEO_DURATION = "duration"
        // 私有参数
        private val REQURST_CODE = 100
    }

    // 视频列表
    private lateinit var mGridView: GridView
    // 其他参数
    private var mImageThumbSize: Int = 0
    private var mImageThumbSpacing: Int = 0
    private lateinit var mAdapter: ImageAdapter
    private lateinit var mImageResizer: ImageResizer
    private var mList = mutableListOf<VideoEntity>()

    override fun getLayout(): Int = R.layout.em_image_grid_activity

    override fun initEventAndData(savedInstanceState: Bundle?) {
        // 初始化控件
        mGridView = find<GridView>(R.id.gridView)
        // 初始化参数
        mImageThumbSize = resources.getDimensionPixelSize(R.dimen.image_thumbnail_size)
        mImageThumbSpacing = resources.getDimensionPixelSize(R.dimen.image_thumbnail_spacing)
        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        val cacheParams = ImageCache.ImageCacheParams()
        // Set memory cache to 25% of app memory
        cacheParams.setMemCacheSizePercent(0.25f)
        mImageResizer = ImageResizer(this, mImageThumbSize)
        mImageResizer.setLoadingImage(R.drawable.em_empty_photo)
        mImageResizer.addImageCache(this.supportFragmentManager, cacheParams)
        getVideoFile()
        mAdapter = ImageAdapter(this, mList, mImageResizer)
        // 绑定适配器
        mGridView.adapter = mAdapter
    }

    override fun bindListener() {
        mGridView.onItemClickListener = this
        mGridView.setOnScrollListener(this)
        mGridView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    public override fun onResume() {
        super.onResume()
        mImageResizer.setExitTasksEarly(false)
        mAdapter.notifyDataSetChanged()
    }

    @SuppressLint("MissingSuperCall")
    public override fun onDestroy() {
        super.onDestroy()
        mImageResizer.closeCache()
        mImageResizer.clearCache()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQURST_CODE) {
            val uri = data?.getParcelableExtra<Uri>(RecorderVideoActivity.VIDEO_URI)
            val projects = arrayOf(MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION)
            var cursor = contentResolver.query(uri, projects, null, null, null)
            var duration = 0
            var filePath: String? = null
            if (cursor.moveToFirst()) {
                // path：MediaStore.Audio.Media.DATA
                filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                // duration：MediaStore.Audio.Media.DURATION
                duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
            }
            if (cursor != null) {
                cursor.close()
                cursor = null
            }
            setResult(Activity.RESULT_OK, intent.putExtra(VIDEO_PATH, filePath).putExtra(VIDEO_DURATION, duration))
            finish()
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
        mImageResizer.setPauseWork(true)
        if (position == 0) {
            val intent = Intent(this, RecorderVideoActivity::class.java)
            startActivityForResult(intent, REQURST_CODE)
        } else {
            val video = mList[position - 1]
            setResult(RESULT_OK, intent.putExtra(VIDEO_PATH, video.filePath).putExtra(VIDEO_DURATION, video.duration))
            finish()
        }
    }

    override fun onScrollStateChanged(absListView: AbsListView?, scrollState: Int) {
        // Pause fetcher to ensure smoother scrolling when flinging
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
            // Before Honeycomb pause image loading on scroll to help
            // with performance
            if (!Utils.hasHoneycomb()) {
                mImageResizer.setPauseWork(true);
            }
        } else {
            mImageResizer.setPauseWork(false);
        }
    }

    override fun onScroll(p0: AbsListView?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onGlobalLayout() {
        val numColumns = Math.floor((mGridView.width / (mImageThumbSize + mImageThumbSpacing)).toDouble()).toInt()
        if (numColumns > 0) {
            val columnWidth = (mGridView.width / numColumns) - mImageThumbSpacing;
            mAdapter.setItemHeight(columnWidth)
            if (Utils.hasJellyBean()) {
                mGridView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            } else {
                mGridView.viewTreeObserver.removeGlobalOnLayoutListener(this)
            }
        }
    }

    @SuppressLint("Recycle")
    private fun getVideoFile() {
        var cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.DEFAULT_SORT_ORDER)
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // ID:MediaStore.Audio.Media._ID
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                // title：MediaStore.Audio.Media.TITLE
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                // path：MediaStore.Audio.Media.DATA
                val url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                // duration：MediaStore.Audio.Media.DURATION
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                // 大小：MediaStore.Audio.Media.SIZE
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)).toInt()
                // 填充集合
                mList.add(VideoEntity(id, title, url, duration, size))
            } while (cursor.moveToNext())
        }
        if (cursor != null) {
            cursor.close()
            cursor = null
        }
    }
}