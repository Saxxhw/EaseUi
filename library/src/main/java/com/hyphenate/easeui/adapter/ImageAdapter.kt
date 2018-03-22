package com.hyphenate.easeui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.hyphenate.easeui.R
import com.hyphenate.easeui.model.VideoEntity
import com.hyphenate.easeui.utils.video.ImageResizer
import com.hyphenate.easeui.widget.RecyclingImageView
import com.hyphenate.util.DateUtils
import com.hyphenate.util.TextFormater
import org.jetbrains.anko.find

/**
 * Created by Saxxhw on 2018/3/22.
 * 邮箱：Saxxhw@126.com
 * 功能：
 */
class ImageAdapter(private val mContext: Context, private val mList: List<VideoEntity>, private val mImageResizer: ImageResizer) : BaseAdapter() {

    private var mItemHeight = 0
    private var mImageViewLayoutParams: RelativeLayout.LayoutParams? = null

    /**
     * Sets the item height. Useful for when we know the column width so the
     * height can be set to match.
     *
     * @param height
     */
    fun setItemHeight(height: Int) {
        if (height == mItemHeight) {
            return
        }
        mItemHeight = height
        mImageViewLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mItemHeight)
        mImageResizer.setImageSize(height)
        notifyDataSetChanged()
    }

    init {
        mImageViewLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
    }

    override fun getView(position: Int, view: View?, container: ViewGroup?): View {
        var convertView = view
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()
            convertView = LayoutInflater.from(mContext).inflate(R.layout.em_choose_griditem, container, false);
            holder.imageView = convertView.find<RecyclingImageView>(R.id.imageView)
            holder.icon = convertView.find<ImageView>(R.id.video_icon)
            holder.tvDur = convertView.find<TextView>(R.id.chatting_length_iv)
            holder.tvSize = convertView.find<TextView>(R.id.chatting_size_iv)
            holder.imageView?.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.imageView?.layoutParams = mImageViewLayoutParams
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        // Check the height matches our calculated column width
        if (holder.imageView?.layoutParams?.height != mItemHeight) {
            holder.imageView?.layoutParams = mImageViewLayoutParams;
        }
        // Finally load the image asynchronously into the ImageView, this also takes care of setting a placeholder image while the background thread runs
        val st1 = mContext.resources.getString(R.string.Video_footage)
        if (position == 0) {
            holder.icon?.visibility = View.GONE
            holder.tvDur?.visibility = View.GONE
            holder.tvSize?.text = st1
            holder.imageView?.setImageResource(R.drawable.em_actionbar_camera_icon)
        } else {
            holder.icon?.visibility = View.VISIBLE
            val entity = mList[position - 1]
            holder.tvDur?.visibility = View.VISIBLE
            holder.tvDur?.text = DateUtils.toTime(entity.duration)
            holder.tvSize?.text = TextFormater.getDataSize(entity.size.toLong())
            holder.imageView?.setImageResource(R.drawable.em_empty_photo)
            mImageResizer.loadImage(entity.filePath, holder.imageView)
        }
        return convertView!!
    }

    override fun getItem(p0: Int): Any? = if (p0 == 0) null else mList[p0 - 1]

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getCount(): Int = mList.size + 1

    class ViewHolder {
        var imageView: RecyclingImageView? = null
        var icon: ImageView? = null
        var tvDur: TextView? = null
        var tvSize: TextView? = null
    }
}