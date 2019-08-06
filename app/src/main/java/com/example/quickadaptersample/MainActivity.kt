package com.example.quickadaptersample

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.quick.adapter.QuickAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = Adapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
        for (index in 0..11) {
            adapter.add(BeanKotlin())
        }
    }

    class Adapter : BaseAdapter<BeanKotlin>() {
        override fun onResultLayoutResId(viewType: Int): Int = R.layout.item_main

        override fun onBindData(holder: BaseViewHolder, position: Int, itemData: BeanKotlin, viewType: Int) {
            holder.setText(R.id.titleTv, "位置：$position") { view, vh ->
                vh.setText(R.id.titleTv,"点击了")
                val view=vh.getView<TextView>(R.id.titleTv)
                print("位置：$position")
            }
        }
    }

    abstract class BaseAdapter<M>:QuickAdapter<M, BaseAdapter.BaseViewHolder>() {
        override fun onResultViewHolder(itemView: View): BaseViewHolder =BaseViewHolder(itemView)

        class BaseViewHolder(itemView: View) : QuickAdapter.ViewHolder(itemView) {
            override fun bindImg(context: Context, url: String, imageView: ImageView?): ViewHolder {
                return this
            }

            override fun bindImgCircle(context: Context, url: String, imageView: ImageView?): ViewHolder {
                return super.bindImgCircle(context, url, imageView)
            }

            override fun bindImgRoundRect(
                context: Context,
                url: String,
                radius: Float,
                imageView: ImageView?
            ): ViewHolder {
                return super.bindImgRoundRect(context, url, radius, imageView)
            }
        }
    }
}
