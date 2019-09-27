package com.example.quickadaptersample

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import org.quick.adapter.QuickAdapter
import org.quick.viewHolder.VHService

class MainActivity : AppCompatActivity() {

    var adapter = Adapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
        addHeaderBtn.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.item_main_include, null)
            view.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            adapter.addHeader(view)
        }
        addFooterBtn.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.item_main_include, null)
            view.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            adapter.addFooter(view)
        }
        addDataBtn.setOnClickListener {
//            val datas = mutableListOf<BeanKotlin>()
//            for (index in 0 until 10) {
//                datas.add(BeanKotlin())
//            }
            adapter.add(BeanKotlin())
        }
        addAllBtn.setOnClickListener {
            val datas = mutableListOf<BeanKotlin>()
            for (index in 0 until 10) {
                datas.add(BeanKotlin())
            }
            adapter.dataList(datas)
        }

        removeAllBtn.setOnClickListener {
            adapter.removeAll()
//            adapter.mFooterViews.clear()
//            adapter.mHeaderViews.clear()
//            adapter.notifyItemRangeRemoved(0, adapter.itemCount)
        }
        removeAllDataBtn.setOnClickListener {
            adapter.removeAll()
        }
    }

    inner class Adapter : BaseAdapter<BeanKotlin>() {
        override fun onResultLayoutResId(viewType: Int): Int = R.layout.item_main
        override fun onBindViewHolderHeader(holder: BaseViewHolder, position: Int) {
            holder.setText(R.id.titleTv, "header：$position") { view, vh ->
                //                vh.setText(R.id.titleTv, "点击了header:$position")
                adapter.removeHeaderAt(holder.adapterPosition)
            }

        }

        override fun onBindViewHolderFooter(holder: BaseViewHolder, position: Int) {
            holder.setText(R.id.titleTv, "footer：$position") { view, vh ->
                //                vh.setText(R.id.titleTv, "点击了footer:$position")
                adapter.removeFooterAt(holder.adapterPosition)
//                adapter.remove(getItem(position))
            }
        }

        override fun onBindData(
            holder: BaseViewHolder,
            position: Int,
            itemData: BeanKotlin,
            viewType: Int
        ) {
            holder.setText(R.id.titleTv, "位置：$position") { view, vh ->
                //                vh.setText(R.id.titleTv, "点击了$position")
//                    .setImg(R.id.coverIv,"http://www.baidu.com")
//                adapter.remove(holder.adapterPosition)
                adapter.remove(getItem(position))
            }.setImg(R.id.coverIv, "http://www.baidu.com")
                .setImgCircle(R.id.coverIv, "http://www.baidu.com")
                .setImgRoundRect(R.id.coverIv, 10f, "http://www.baidu.com")
        }
    }

    abstract class BaseAdapter<M> : QuickAdapter<M, BaseAdapter.BaseViewHolder>() {
        override fun onResultViewHolder(itemView: View): BaseViewHolder = BaseViewHolder(itemView)

        class BaseViewHolder(itemView: View) : QuickAdapter.ViewHolder(itemView) {

            override fun setText(
                id: Int,
                content: CharSequence?,
                onClickListener: ((view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit)?
            ): VHService {
                return super.setText(id, content, onClickListener)
            }

            override fun setImg(
                id: Int,
                url: CharSequence,
                onClickListener: ((view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit)?
            ): VHService {
                return super.setImg(id, url, onClickListener)
            }

            override fun setImg(
                id: Int,
                iconId: Int,
                onClickListener: ((view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit)?
            ): VHService {
                return super.setImg(id, iconId, onClickListener)
            }

            override fun bindImg(context: Context, url: String, imageView: ImageView?): VHService {
                return this
            }

            override fun bindImgCircle(
                context: Context,
                url: String,
                imageView: ImageView?
            ): VHService {
                return super.bindImgCircle(context, url, imageView)
            }

            override fun bindImgRoundRect(
                context: Context,
                url: String,
                radius: Float,
                imageView: ImageView?
            ): VHService {
                return super.bindImgRoundRect(context, url, radius, imageView)
            }
        }
    }
}
