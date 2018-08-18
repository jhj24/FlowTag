package com.jhj.flowtaglayout

import android.view.LayoutInflater
import android.view.View
import java.util.*

class TagAdapter<T>(dataList: List<T>) {
    private var dataList: List<T>? = dataList
    private var mOnDataChangedListener: OnDataChangedListener? = null
    internal val preCheckedList = HashSet<Int>()
    private var layoutRes: Int = 0


    val count: Int
        get() = dataList?.size ?: 0

    fun setLayoutRes(layoutRes: Int) {
        this.layoutRes = layoutRes
    }

    internal interface OnDataChangedListener {
        fun onChanged()
    }

    internal fun setOnDataChangedListener(listener: OnDataChangedListener) {
        mOnDataChangedListener = listener
    }

    fun setSelectedList(vararg poses: Int) {
        val set = HashSet<Int>()
        for (pos in poses) {
            set.add(pos)
        }
        setSelectedList(set)
    }

    private fun setSelectedList(set: Set<Int>?) {
        preCheckedList.clear()
        set?.let {
            preCheckedList.addAll(it)
        }
        notifyDataChanged()
    }

    private fun notifyDataChanged() {
        if (mOnDataChangedListener != null)
            mOnDataChangedListener?.onChanged()
    }

    fun getView(parent: FlowLayout): View {
        val inflater = LayoutInflater.from(parent.context)
        return inflater.inflate(layoutRes, parent, false)
    }

}
