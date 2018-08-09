package com.jhj.flowtag

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.jhj.flowtaglayout.FlowLayout
import com.jhj.flowtaglayout.TagFlowLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_selected.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = arrayListOf<String>("Hello", "Android", "Welcome Hi ", "Button", "TextView", "Hello",
                "Android", "Welcome", "Button ImageView", "TextView", "Helloworld",
                "Android", "Welcome Hello", "Button Text", "TextView")

        val layout = findViewById<TagFlowLayout<String>>(R.id.tagFlowLayout)

        btn_multi.setOnClickListener {
            layout
                    .setLayoutRes(list, R.layout.layout_selected, object : TagFlowLayout.OnCustomListener {
                        override fun onLayout(view: View, pos: Int) {
                            view.tv_name.text = list[pos]
                        }
                    })
                    .setMaxSelectCount(-1)
                    .setOnTagClickListener(object : TagFlowLayout.OnTagClickListener {
                        override fun onTagClick(view: View, position: Int, parent: FlowLayout) {
                            toast(list[position])
                        }
                    })
        }

        btn_single.setOnClickListener {
            layout
                    .setLayoutRes(list, R.layout.layout_selected, object : TagFlowLayout.OnCustomListener {
                        override fun onLayout(view: View, pos: Int) {
                            view.tv_name.text = list[pos]
                        }
                    })
                    .setMaxSelectCount(1)
                    .setOnTagClickListener(object : TagFlowLayout.OnTagClickListener {
                        override fun onTagClick(view: View, position: Int, parent: FlowLayout) {
                            toast(list[position])
                        }
                    })
        }

        btn_clicked.setOnClickListener {

        }

        btn_limit.setOnClickListener {
            layout
                    .setLayoutRes(list, R.layout.layout_selected, object : TagFlowLayout.OnCustomListener {
                        override fun onLayout(view: View, pos: Int) {
                            view.tv_name.text = list[pos]
                        }
                    })
                    .setMaxSelectCount(3)
                    .setOnBeyondMaxSelectListener(object : TagFlowLayout.OnBeyondMaxSelectListener {
                        override fun onSelected(position: Int, maxSelected: Int) {
                            toast("最多选择数为${layout.getMaxSelected()}")
                        }
                    })
                    .setOnTagClickListener(object : TagFlowLayout.OnTagClickListener {
                        override fun onTagClick(view: View, position: Int, parent: FlowLayout) {
                            toast(list[position])
                        }
                    })
        }
    }
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
