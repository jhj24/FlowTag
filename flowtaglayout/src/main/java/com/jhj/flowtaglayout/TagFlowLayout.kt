package com.jhj.flowtaglayout

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Created by zhy on 15/9/10.
 */
class TagFlowLayout<T> : FlowLayout, TagAdapter.OnDataChangedListener {

    var adapter: TagAdapter<T>? = null
    private var mSelectedMax = -1//-1为不限制数量
    private val mSelectedView = HashSet<Int>()
    private var mOnSelectListener: OnSelectListener? = null
    private var mOnTagClickListener: OnTagClickListener? = null
    private var mOnBeyondMaxSelectListener: OnBeyondMaxSelectListener? = null
    private var customListener: OnCustomListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : super(context, attrs, defStyle) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout)
        mSelectedMax = ta.getInt(R.styleable.TagFlowLayout_max_select, -1)
        ta.recycle()
    }

    val selectedList: Set<Int>
        get() = HashSet(mSelectedView)




    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val cCount = childCount
        for (i in 0 until cCount) {
            val tagView = getChildAt(i) as TagView
            if (tagView.visibility == View.GONE) {
                continue
            }
            if (tagView.tagView.visibility == View.GONE) {
                tagView.visibility = View.GONE
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    fun setOnSelectListener(onSelectListener: OnSelectListener): TagFlowLayout<T> {
        mOnSelectListener = onSelectListener
        return this
    }


    fun setOnTagClickListener(onTagClickListener: OnTagClickListener): TagFlowLayout<T> {
        mOnTagClickListener = onTagClickListener
        return this
    }


    fun setOnBeyondMaxSelectListener(onBeyondMaxSelectListener: OnBeyondMaxSelectListener): TagFlowLayout<T> {
        mOnBeyondMaxSelectListener = onBeyondMaxSelectListener
        return this
    }

    fun setLayoutRes(dataList: List<T>, layoutRes: Int): TagFlowLayout<T> {
        adapter = TagAdapter<T>(dataList)
        adapter!!.setLayoutRes(layoutRes)
        adapter!!.setOnDataChangedListener(this)
        mSelectedView.clear()
        changeAdapter()
        return this
    }

    fun setLayoutRes(dataList: List<T>, layoutRes: Int, listener: OnCustomListener): TagFlowLayout<T> {
        this.customListener = listener
        this.setLayoutRes(dataList, layoutRes)
        return this
    }

    fun setSelectedList(vararg poses: Int): TagFlowLayout<T> {
        adapter!!.setSelectedList(*poses)
        return this
    }

    fun getMaxSelected(): Int {
        return mSelectedMax
    }



    private fun changeAdapter() {
        removeAllViews()
        val adapter = this.adapter
        var tagViewContainer: TagView? = null
        val preCheckedList = this.adapter!!.preCheckedList
        for (i in 0 until adapter!!.count) {
            val tagView = adapter.getView(this)
            if (customListener != null) {
                customListener!!.onLayout(tagView, i)
            }
            tagViewContainer = TagView(context)
            tagView.isDuplicateParentStateEnabled = true
            if (tagView.layoutParams != null) {
                tagViewContainer.layoutParams = tagView.layoutParams


            } else {
                val lp = ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.setMargins(dip2px(context, 5f),
                        dip2px(context, 5f),
                        dip2px(context, 5f),
                        dip2px(context, 5f))
                tagViewContainer.layoutParams = lp
            }
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            tagView.layoutParams = lp
            tagViewContainer.addView(tagView)
            addView(tagViewContainer)

            if (preCheckedList.contains(i)) {
                setChildChecked(i, tagViewContainer)
            }

            if (this.adapter?.setSelected(i, adapter.getItem(i)) == true) {
                setChildChecked(i, tagViewContainer)
            }
            tagView.isClickable = false
            val finalTagViewContainer = tagViewContainer
            val position = i
            tagViewContainer.setOnClickListener {
                doSelect(finalTagViewContainer, i)
                if (mOnTagClickListener != null) {
                    mOnTagClickListener!!.onTagClick(finalTagViewContainer, i, this@TagFlowLayout)
                }
            }
        }
        mSelectedView.addAll(preCheckedList)
    }

    fun setMaxSelectCount(count: Int): TagFlowLayout<T> {
        if (mSelectedView.size > count) {
            Log.w(TAG, "you has already select more than $count views , so it will be clear .")
            mSelectedView.clear()
        }
        mSelectedMax = count
        return this
    }

    private fun setChildChecked(position: Int, view: TagView) {
        view.isChecked = true
        //adapter!!.onSelected(position, view.tagView)
    }

    private fun setChildUnChecked(position: Int, view: TagView) {
        view.isChecked = false
        // adapter!!.unSelected(position, view.tagView)
    }

    private fun doSelect(child: TagView, position: Int) {
        if (!child.isChecked) {
            //处理max_select=1的情况
            if (mSelectedMax == 1 && mSelectedView.size == 1) {
                val iterator = mSelectedView.iterator()
                val preIndex = iterator.next()
                val pre = getChildAt(preIndex) as TagView
                setChildUnChecked(preIndex, pre)
                setChildChecked(position, child)

                mSelectedView.remove(preIndex)
                mSelectedView.add(position)
            } else {
                if (mSelectedMax > 0 && mSelectedView.size >= mSelectedMax) {
                    if (mOnBeyondMaxSelectListener != null) {
                        mOnBeyondMaxSelectListener!!.onSelected(position, mSelectedMax)
                    }
                    return
                }
                setChildChecked(position, child)
                mSelectedView.add(position)
            }
        } else {
            setChildUnChecked(position, child)
            mSelectedView.remove(position)
        }
        if (mOnSelectListener != null) {
            mOnSelectListener!!.onSelected(HashSet(mSelectedView))
        }
    }


    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(KEY_DEFAULT, super.onSaveInstanceState())

        var selectPos = ""
        if (mSelectedView.size > 0) {
            for (key in mSelectedView) {
                selectPos += key.toString() + "|"
            }
            selectPos = selectPos.substring(0, selectPos.length - 1)
        }
        bundle.putString(KEY_CHOOSE_POS, selectPos)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val bundle = state
            val mSelectPos = state.getString(KEY_CHOOSE_POS)
            if (!TextUtils.isEmpty(mSelectPos)) {
                val split = mSelectPos!!.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (pos in split) {
                    val index = Integer.parseInt(pos)
                    mSelectedView.add(index)

                    val tagView = getChildAt(index) as TagView
                    if (tagView != null) {
                        setChildChecked(index, tagView)
                    }
                }

            }
            super.onRestoreInstanceState(state.getParcelable(KEY_DEFAULT))
            return
        }
        super.onRestoreInstanceState(state)
    }


    override fun onChanged() {
        mSelectedView.clear()
        changeAdapter()
    }

    companion object {
        private val TAG = "TagFlowLayout"


        private val KEY_CHOOSE_POS = "key_choose_pos"
        private val KEY_DEFAULT = "key_default"

        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }

    interface OnCustomListener {
        fun onLayout(view: View, pos: Int)
    }

    interface OnSelectListener {
        fun onSelected(selectPosSet: Set<Int>)
    }

    interface OnTagClickListener {
        fun onTagClick(view: View, position: Int, parent: FlowLayout)
    }

    interface OnBeyondMaxSelectListener {
        fun onSelected(position: Int, maxSelected: Int)
    }

}
