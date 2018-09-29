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

class TagFlowLayout<T> : FlowLayout, TagAdapter.OnDataChangedListener {

    private var isClicked = true
    private var adapter: TagAdapter<T>? = null
    private var mSelectedMax = -1//-1为不限制数量
    private val mSelectedView = HashSet<Int>()
    private var mOnSelectListener: OnSelectListener? = null
    private var mOnTagClickListener: OnTagClickListener? = null
    private var mOnBeyondMaxSelectListener: OnBeyondMaxSelectListener? = null
    private var mCustomerListener: OnCustomListener? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : super(context, attrs, defStyle) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout)
        mSelectedMax = ta.getInt(R.styleable.TagFlowLayout_max_select, -1)
        ta.recycle()
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

    fun setDataList(dataList: List<T>): TagFlowLayout<T> {
        adapter = TagAdapter(dataList)
        return this
    }

    fun setLayoutRes(layoutRes: Int): TagFlowLayout<T> {
        adapter?.setLayoutRes(layoutRes)
        adapter?.setOnDataChangedListener(this)
        mSelectedView.clear()
        changeAdapter()
        return this
    }

    fun setLayoutRes(layoutRes: Int, listener: OnCustomListener): TagFlowLayout<T> {
        this.mCustomerListener = listener
        this.setLayoutRes(layoutRes)
        return this
    }

    fun getSelectedList(): Set<Int> {
        return HashSet(mSelectedView)
    }

    fun setSelectedList(vararg poses: Int): TagFlowLayout<T> {
        adapter?.setSelectedList(*poses)
        return this
    }

    fun getMaxSelectedCount(): Int {
        return mSelectedMax
    }

    fun setMaxSelectCount(count: Int): TagFlowLayout<T> {
        if (mSelectedView.size > count) {
            Log.w(TAG, "you has already select more than $count views , so it will be clear .")
            mSelectedView.clear()
        }
        mSelectedMax = count
        return this
    }

    fun setClicked(isClickable: Boolean): TagFlowLayout<T> {
        this.isClicked = isClickable
        return this
    }


    private fun changeAdapter() {
        removeAllViews()
        adapter?.let {
            for (i in 0 until it.count) {
                val layout = it.getView(this)
                if (mCustomerListener != null) {
                    mCustomerListener?.onLayout(layout, i)
                }
                val tagView = TagView(context)
                layout.isDuplicateParentStateEnabled = true
                if (layout.layoutParams != null) {
                    tagView.layoutParams = layout.layoutParams
                } else {
                    val lp = ViewGroup.MarginLayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT)
                    lp.setMargins(dip2px(context, 5f),
                            dip2px(context, 5f),
                            dip2px(context, 5f),
                            dip2px(context, 5f))
                    tagView.layoutParams = lp
                }
                val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                layout.layoutParams = lp
                tagView.addView(layout)
                addView(tagView)

                if (it.preCheckedList.contains(i)) {
                    tagView.isChecked = true
                }
                tagView.setOnClickListener {
                    if (mSelectedMax != 0) {
                        doSelect(tagView, i)
                    }
                    if (mOnTagClickListener != null) {
                        mOnTagClickListener?.onTagClick(tagView, i, this@TagFlowLayout)
                    }
                }
                tagView.isClickable = isClicked
            }
            mSelectedView.addAll(it.preCheckedList)
        }

    }


    private fun doSelect(child: TagView, position: Int) {
        if (!child.isChecked) {
            //处理max_select=1的情况
            if (mSelectedMax == 1 && mSelectedView.size == 1) {
                val iterator = mSelectedView.iterator()
                val preIndex = iterator.next()
                val pre = getChildAt(preIndex) as TagView
                pre.isChecked = false
                child.isChecked = true

                mSelectedView.remove(preIndex)
                mSelectedView.add(position)
            } else {
                if (mSelectedMax > 0 && mSelectedView.size >= mSelectedMax) {
                    if (mOnBeyondMaxSelectListener != null) {
                        mOnBeyondMaxSelectListener?.onSelected(position, mSelectedMax)
                    }
                    return
                }
                child.isChecked = true
                mSelectedView.add(position)
            }
        } else {
            child.isChecked = false
            mSelectedView.remove(position)
        }
        if (mOnSelectListener != null) {
            mOnSelectListener?.onSelected(HashSet(mSelectedView))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for (i in 0 until childCount) {
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

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val mSelectPos = state.getString(KEY_CHOOSE_POS)
            if (!TextUtils.isEmpty(mSelectPos)) {
                val split = mSelectPos?.split("\\|".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                split?.let {
                    for (pos in it) {
                        val index = Integer.parseInt(pos)
                        mSelectedView.add(index)
                        val tagView = getChildAt(index) as TagView
                        tagView.isChecked = true
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
        private const val TAG = "TagFlowLayout"
        private const val KEY_CHOOSE_POS = "key_choose_pos"
        private const val KEY_DEFAULT = "key_default"

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
