package com.jhj.flowtag

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.jhj.flowtaglayout.FlowLayout
import com.jhj.flowtaglayout.TagFlowLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = arrayListOf<String>("Hello", "Android", "Welcome Hi ", "Button", "TextView", "Hello",
                "Android", "Welcome", "Button ImageView", "TextView", "Helloworld",
                "Android", "Welcome Hello", "Button Text", "TextView")

        val layout = findViewById<TagFlowLayout<String>>(R.id.tagFlowLayout)

        val textView = TextView(this)
        textView.setTextColor(R.drawable.bg_tag_text_color)
        textView.setBackgroundColor(R.drawable.bg_tag_view_color)

        linearLayout.setOnClickListener {

        }

        btn_clicked.setOnClickListener {
            layout
                    .setDataList(list)
                    .setLayoutRes(R.layout.layout_selected, object : TagFlowLayout.OnCustomListener {
                        override fun onLayout(view: View, pos: Int) {
                            (view as TextView).text = list[pos]
                        }
                    })
                    .setMaxSelectCount(0)
                    .setOnTagClickListener(object : TagFlowLayout.OnTagClickListener {
                        override fun onTagClick(view: View, position: Int, parent: FlowLayout) {
                            toast(list[position])
                        }
                    })

        }


        btn_single.setOnClickListener {
            layout
                    .setDataList(list)
                    .setLayoutRes(R.layout.layout_selected, object : TagFlowLayout.OnCustomListener {
                        override fun onLayout(view: View, pos: Int) {
                            (view as TextView).text = list[pos]
                        }
                    })
                    .setMaxSelectCount(1)
                    .setOnTagClickListener(object : TagFlowLayout.OnTagClickListener {
                        override fun onTagClick(view: View, position: Int, parent: FlowLayout) {
                            toast(list[position])
                        }
                    })
        }


        btn_multi.setOnClickListener {
            layout
                    .setDataList(list)
                    .setLayoutRes(R.layout.layout_selected, object : TagFlowLayout.OnCustomListener {
                        override fun onLayout(view: View, pos: Int) {
                            (view as TextView).text = list[pos]
                        }
                    })
                    .setMaxSelectCount(-1)
                    .setOnTagClickListener(object : TagFlowLayout.OnTagClickListener {
                        override fun onTagClick(view: View, position: Int, parent: FlowLayout) {
                            toast(list[position])
                        }
                    })
                    .setSelectedList(1, 2)
        }

        btn_limit.setOnClickListener {
            layout
                    .setDataList(list)
                    .setLayoutRes(R.layout.layout_selected, object : TagFlowLayout.OnCustomListener {
                        override fun onLayout(view: View, pos: Int) {
                            (view as TextView).text = list[pos]
                        }
                    })
                    .setMaxSelectCount(3)
                    .setOnBeyondMaxSelectListener(object : TagFlowLayout.OnBeyondMaxSelectListener {
                        override fun onSelected(position: Int, maxSelected: Int) {
                            toast("最多选择数为${layout.getMaxSelectedCount()}")
                        }
                    })

        }
    }
}

fun Activity.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
