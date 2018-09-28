package com.jhj.flowtaglayout

import android.content.Context
import android.view.View
import android.widget.Checkable
import android.widget.FrameLayout

/**
 * TagView
 */
class TagView(context: Context) : FrameLayout(context), Checkable {
    private var isChecked: Boolean = false

    val tagView: View
        get() = getChildAt(0)

    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val states = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked()) {
            View.mergeDrawableStates(states, CHECK_STATE)
        }
        return states
    }


    override fun setChecked(checked: Boolean) {
        if (this.isChecked != checked) {
            this.isChecked = checked
            refreshDrawableState()
        }
    }

    override fun isChecked(): Boolean {
        return isChecked
    }

    /**
     * 将已检查的视图状态更改为其当前状态的反转
     */
    override fun toggle() {
        setChecked(!isChecked)
    }

    companion object {
        private val CHECK_STATE = intArrayOf(android.R.attr.state_checked)
    }


}
