package org.quick.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.CallSuper
import androidx.annotation.Size
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import org.quick.viewHolder.VHService
import org.quick.viewHolder.callback.OnClickListener2

/**
 * Created by chris Zou on 2016/6/12.
 * 基础适配器,继承便能快速生成adapter
 * 如需分割线，请参照quick-library中的XRecyclerViewLine
 * @author chris Zou
 * @Date 2016/6/12
 */
abstract class QuickAdapter<M, H : QuickAdapter.ViewHolder> : RecyclerView.Adapter<H>() {
    lateinit var context: Context
    var parent: RecyclerView? = null

    private val dataList = mutableListOf<M>()

    val mHeaderViews = SparseArray<View>()/*头部*/
    val mFooterViews = SparseArray<View>()/*底部*/

    private var mOnItemClickListener: ((view: View, viewHolder: H, position: Int, itemData: M) -> Unit)? =
        null
    private var mOnItemLongClickListener: ((view: View, viewHolder: H, position: Int, itemData: M) -> Boolean)? =
        null
    private var mOnClickListener: ((view: View, viewHolder: H, position: Int, itemData: M) -> Unit)? =
        null
    private var mOnCheckedChangedListener: ((view: View, viewHolder: H, isChecked: Boolean, position: Int, itemData: M) -> Unit)? =
        null
    private var clickResId: IntArray = intArrayOf()
    private var checkedChangedResId = intArrayOf()

    /**
     * 布局文件
     *
     * @return
     */
    abstract fun onResultLayoutResId(viewType: Int): Int

    abstract fun onBindData(holder: H, position: Int, itemData: M, viewType: Int)

    override fun getItemCount(): Int = mHeaderViews.size() + mFooterViews.size() + dataList.size

    @CallSuper
    override fun getItemViewType(position: Int): Int = when {
        isHeaderView(position) ->
            mHeaderViews.keyAt(position)
        isFooterView(position) -> {
            val topSize = mHeaderViews.size() + dataList().size
            mFooterViews.keyAt(position - topSize)
        }
        else ->
            getRealDataPosition(position)
    }

    /**
     * 上下左右的padding
     *
     * @return
     */
    open fun onResultMargin(position: Int): Float {
        return 0.0f
    }

    /**
     * 上下左右的padding
     *
     * @return
     */
    open fun onResultMarginTop(position: Int): Float {
        return if (onResultMargin(position) > 0) onResultMargin(position) / 2 else onResultMargin(
            position
        )
    }

    /**
     * 上下左右的padding
     *
     * @return
     */
    open fun onResultMarginBottom(position: Int): Float {
        return if (onResultMargin(position) > 0) onResultMargin(position) / 2 else onResultMargin(
            position
        )
    }

    /**
     * 上下左右的padding
     *
     * @return
     */
    open fun onResultMarginLeft(position: Int): Float {
        return onResultMargin(position)
    }

    /**
     * 上下左右的padding
     *
     * @return
     */
    open fun onResultMarginRight(position: Int): Float {
        return onResultMargin(position)
    }

    open fun onResultPadding(position: Int): Float {
        return 0.0f
    }

    /**
     * 上下左右的padding
     *
     * @return
     */
    open fun onResultPaddingTop(position: Int): Float {
        return if (onResultPadding(position) > 0) onResultPadding(position) / 2 else onResultPadding(
            position
        )
    }

    /**
     * 上下左右的padding
     *
     * @return
     */
    open fun onResultPaddingBottom(position: Int): Float {
        return if (onResultPadding(position) > 0) onResultPadding(position) / 2 else onResultPadding(
            position
        )
    }

    /**
     * 上下左右的padding
     *
     * @return
     */
    open fun onResultPaddingLeft(position: Int): Float {
        return onResultPadding(position)
    }

    /**
     * 上下左右的padding
     *
     * @return
     */
    open fun onResultPaddingRight(position: Int): Float {
        return onResultPadding(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): H {
        context = parent.context
        return when {
            mHeaderViews.get(viewType) != null -> onResultViewHolder(mHeaderViews.get(viewType))
            mFooterViews.get(viewType) != null -> onResultViewHolder(mFooterViews.get(viewType))
            else -> setupLayout(
                LayoutInflater.from(context).inflate(
                    onResultLayoutResId(viewType),
                    parent,
                    false
                )
            )
        }
    }

    open fun setupLayout(itemView: View): H {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (itemView.foreground == null)
                itemView.foreground = ContextCompat.getDrawable(
                    context,
                    Utils.getSystemAttrTypeValue(
                        context,
                        R.attr.selectableItemBackgroundBorderless
                    ).resourceId
                )
        } else if (itemView.background == null)
            itemView.setBackgroundResource(
                Utils.getSystemAttrTypeValue(
                    context,
                    R.attr.selectableItemBackground
                ).resourceId
            )

        return onResultViewHolder(itemView)
    }

    open fun onResultViewHolder(itemView: View): H = ViewHolder(itemView) as H

    open fun onBindViewHolderHeader(holder: H, position: Int) = Unit

    open fun onBindViewHolderFooter(holder: H, position: Int) = Unit

    override fun onBindViewHolder(holder: H, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        when {
            isHeaderView(position) -> {
                onBindViewHolderHeader(holder, position)
            }
            isFooterView(position) -> {
                val topSize = mHeaderViews.size() + dataList().size
                onBindViewHolderFooter(holder, position - topSize)
            }
            else -> {
                val realPosition = getRealDataPosition(position)
                setupListener(holder)
                setupLayout(holder, realPosition)
                onBindData(
                    holder,
                    realPosition,
                    dataList()[realPosition],
                    getItemViewType(realPosition)
                )
            }
        }
    }

    /**
     * 设置各种监听
     *
     * @param holder
     */
    private fun setupListener(holder: H) {
        /*单击事件*/
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(object : OnClickListener2() {
                override fun click(view: View) {
                    val dataIndex = getRealDataPosition(holder.adapterPosition)
                    mOnItemClickListener?.invoke(view, holder, dataIndex, getItem(dataIndex))
                }
            })
        }
        /*长按事件*/
        if (mOnItemLongClickListener != null) holder.itemView.setOnLongClickListener { v ->
            val dataIndex = getRealDataPosition(holder.adapterPosition)
            mOnItemLongClickListener!!.invoke(v, holder, dataIndex, getItem(dataIndex))
        }
        /*选择事件*/
        if (mOnCheckedChangedListener != null && checkedChangedResId.isNotEmpty()) {
            for (resId in checkedChangedResId) {
                val compoundButton = holder.getView<View>(resId)
                if (compoundButton is CompoundButton)
                    compoundButton.setOnCheckedChangeListener { buttonView, isChecked ->
                        val dataIndex = getRealDataPosition(holder.adapterPosition)
                        mOnCheckedChangedListener?.invoke(
                            buttonView,
                            holder,
                            isChecked,
                            dataIndex,
                            getItem(dataIndex)
                        )
                    }
                else
                    Log.e(
                        "列表选择事件错误：",
                        String.format(
                            "from%s id:%d类型不正确，无法设置OnCheckedChangedListener",
                            context.javaClass.simpleName,
                            resId
                        )
                    )
            }
        }
        /*item项内View的独立点击事件，与OnItemClickListner不冲突*/
        if (mOnClickListener != null && clickResId.isNotEmpty()) {
            holder.setOnClick({ view, vh ->
                val dataIndex = getRealDataPosition(holder.adapterPosition)
                mOnClickListener?.invoke(view, holder, dataIndex, getItem(dataIndex))
            }, *clickResId)
        }
    }

    /**
     * 设置布局
     *
     * @param holder
     * @param position
     */
    private fun setupLayout(holder: H, position: Int) {

        var left = onResultMarginLeft(position).toInt()
        var top = onResultMarginTop(position).toInt()
        var right = onResultMarginRight(position).toInt()
        var bottom =
            when {
                position == itemCount - 1 -> onResultMarginBottom(position).toInt()
                onResultMarginBottom(position) > 0 -> onResultMarginBottom(position).toInt() / 2
                else -> 0
            }
        val itemLayoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        itemLayoutParams.setMargins(left, top, right, bottom)

        left = onResultPaddingLeft(position).toInt()
        top = onResultPaddingTop(position).toInt()
        right = onResultPaddingRight(position).toInt()
        bottom =
            when {
                position == itemCount - 1 -> onResultPaddingBottom(position).toInt()
                onResultPaddingBottom(position) > 0 -> onResultPaddingBottom(position).toInt() / 2
                else -> 0
            }
        holder.itemView.setPadding(left, top, right, bottom)
    }

    fun dataList(tempList: MutableList<M>) {
        dataList().clear()
        add(tempList)
    }

    fun dataList(): MutableList<M> {
        return dataList
    }

    fun add(tempList: MutableList<M>) {
        if (tempList.isNotEmpty()) {
            val lastSize = dataList().size + mHeaderViews.size()
            dataList().addAll(tempList)
            notifyItemRangeInserted(lastSize,tempList.size)
        }
    }

    fun add(m: M) {
        dataList().add(m)
        val topSize = mHeaderViews.size() + dataList().size
        notifyItemInserted(topSize)
    }

    /**
     * @param position 真实位置，调用holder.adapterPosition
     */
    fun remove(position: Int) {
        try {
            val index = getRealDataPosition(position)
            dataList.removeAt(index)
            notifyItemRemoved(position)
        } catch (O_O: IndexOutOfBoundsException) {
            O_O.printStackTrace()
        }
    }

    fun remove(m: M) {
        dataList().remove(m)
        val topIndex = mHeaderViews.size()
        notifyItemRemoved(dataList().indexOf(m) + topIndex)
    }

    fun removeAll() {
        val lastDataSize = dataList().size
        dataList().clear()
        notifyItemRangeRemoved(mHeaderViews.size(), lastDataSize)
    }

    fun getItem(position: Int): M {
        return dataList()[position]
    }

    /*head footer相关*/

    /**
     * 添加头部View
     */
    fun addHeader(view: View) {
        val key =
            if (mHeaderViews.size() > 0)
                mHeaderViews.keyAt(mHeaderViews.size() - 1) + 1
            else
                Int.MAX_VALUE / 100
        addHeader(key, view)
    }

    fun addHeader(key: Int, view: View) {
        mHeaderViews.put(key, view)
        val index = mHeaderViews.indexOfKey(key)
        notifyItemInserted(index)
    }

    /**
     * 添加底部View
     */
    fun addFooter(view: View) {
        val key =
            if (mFooterViews.size() > 0)
                mFooterViews.keyAt(mFooterViews.size() - 1) + 1
            else
                Int.MAX_VALUE / 100
        addFooter(key, view)
    }

    fun addFooter(key: Int, view: View) {
        mFooterViews.put(key, view)
        val index = mHeaderViews.size() + dataList().size + mFooterViews.indexOfKey(key) + 1
        notifyItemInserted(index)
    }

    fun removeHeader(view: View) {
        val index = mHeaderViews.indexOfValue(view)
        if (index != -1) {
            mHeaderViews.remove(mHeaderViews.keyAt(index))
            notifyItemRemoved(index)
        } else {
            Log.e(javaClass::class.java.simpleName, "指定View不存在，删除失败")
        }
    }

    /**
     * 以Key的方式删除
     */
    fun removeHeader(key: Int) {
        mHeaderViews.remove(key)
        notifyItemRemoved(mHeaderViews.indexOfKey(key))
    }

    /**
     * @param position 真实位置，调用holder.adapterPosition
     */
    fun removeHeaderAt(position: Int) {
        try {
            mHeaderViews.removeAt(position)
            notifyItemRemoved(position)
        } catch (O_O: IndexOutOfBoundsException) {
            O_O.printStackTrace()
        }
    }

    fun removeFooter(view: View) {
        val index = mFooterViews.indexOfValue(view)
        if (index != -1) {
            removeFooterAt(index)
        } else {
            Log.e(javaClass::class.java.simpleName, "指定View不存在，删除失败")
        }
    }

    fun removeFooter(key: Int) {
        mFooterViews.remove(key)
        val topIndex = mHeaderViews.size() + dataList().size
        val index = mFooterViews.indexOfKey(key)
        notifyItemRemoved(topIndex + index)
    }

    /**
     * @param position 真实位置，调用holder.adapterPosition
     */
    fun removeFooterAt(position: Int) {
        try {
            val topIndex = mHeaderViews.size() + dataList().size
            mFooterViews.removeAt(position - topIndex)
            notifyItemRemoved(position)
        } catch (O_O: IndexOutOfBoundsException) {
            O_O.printStackTrace()
        }
    }

    fun onClick(
        onClickListener: ((view: View, viewHolder: H, position: Int, itemData: M) -> Unit), @Size(
            min = 1
        ) vararg params: Int
    ) {
        this.clickResId = params
        this.mOnClickListener = onClickListener
    }

    fun onCheckedChanged(
        onCheckedChangedListener: ((view: View, viewHolder: H, isChecked: Boolean, position: Int, itemData: M) -> Unit), @Size(
            min = 1
        ) vararg checkedChangedResId: Int
    ) {
        this.checkedChangedResId = checkedChangedResId
        this.mOnCheckedChangedListener = onCheckedChangedListener
    }

    fun onItemClick(onItemClickListener: ((view: View, viewHolder: H, position: Int, itemData: M) -> Unit)) {
        this.mOnItemClickListener = onItemClickListener
    }

    fun onItemLongClick(onItemLongClickListener: ((view: View, viewHolder: H, position: Int, itemData: M) -> Boolean)) {
        this.mOnItemLongClickListener = onItemLongClickListener
    }


    /**
     * 获取实际数据位置
     */
    fun getRealDataPosition(position: Int): Int {
        var realIndex = position - mHeaderViews.size()
        realIndex = when {
            realIndex < 0 -> 0
            realIndex >= dataList().size -> dataList().size - 1
            else -> realIndex
        }
        return realIndex
    }

    fun isHeaderView(view: View) =
        mHeaderViews.size() > 0 && mHeaderViews.indexOfValue(view) != -1

    /**
     * 根据flag判断是否是头部
     * * @param position 下标位置或者是Key
     */
    fun isHeaderView(position: Int): Boolean =
        mHeaderViews.size() > 0 &&
                position < mHeaderViews.size() &&
                mHeaderViews.valueAt(position) != null

    /**
     * 根据flag判断是否是尾总
     * @param position 下标位置或者是Key
     */
    fun isFooterView(position: Int): Boolean {
        val maxIndex = mHeaderViews.size() + dataList().size - 1
        return mFooterViews.size() > 0 && position > maxIndex
    }

    fun isFooterView(view: View) =
        mFooterViews.size() > 0 && mFooterViews.indexOfValue(view) != -1

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        parent = recyclerView
        when {
            /*占一整行*/
            parent!!.layoutManager is GridLayoutManager -> (parent!!.layoutManager as GridLayoutManager).spanSizeLookup =
                object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (isHeaderView(position) || isFooterView(position)) (parent!!.layoutManager as GridLayoutManager).spanCount else 1
                    }
                }
        }
    }

    override fun onViewAttachedToWindow(holder: H) {
        super.onViewAttachedToWindow(holder)
        if (parent?.layoutManager is StaggeredGridLayoutManager && (isHeaderView(holder.itemView) || isFooterView(
                holder.itemView
            ))
        ) (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan =
            true
    }

    /**
     * 是否垂直滚动
     */
    fun isVertically(): Boolean {
        return if (parent != null) {
            parent!!.layoutManager!!.canScrollVertically()
        } else true
    }


    @Suppress("LeakingThis")
    open class ViewHolder(
        itemView: View, private var vh: VH = VH(itemView)
    ) : RecyclerView.ViewHolder(itemView), VHService {

        init {
            vh.bindViewHolder(this)
        }

        override fun <T : View> getView(id: Int): T? = vh.getView<T>(id)

        override fun setText(
            id: Int,
            content: CharSequence?,
            onClickListener: ((view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit)?
        ): VHService {
            vh.setText(id, content, onClickListener)
            return this
        }

        override fun setImg(
            id: Int,
            iconId: Int,
            onClickListener: ((view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit)?
        ): VHService {
            vh.setImg(id, iconId, onClickListener)
            return this
        }

        override fun setImg(
            id: Int,
            url: CharSequence,
            onClickListener: ((view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit)?
        ): VHService {
            vh.setImg(id, url, onClickListener)
            return this
        }

        override fun setImgRoundRect(
            id: Int,
            radius: Float,
            iconId: Int,
            onClickListener: ((view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit)?
        ): VHService {
            vh.setImgRoundRect(id, radius, iconId, onClickListener)
            return this
        }

        override fun setImgRoundRect(
            id: Int,
            radius: Float,
            url: CharSequence,
            onClickListener: ((view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit)?
        ): VHService {
            vh.setImgRoundRect(id, radius, url, onClickListener)
            return this
        }

        override fun setImgCircle(
            id: Int,
            url: CharSequence,
            onClickListener: ((view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit)?
        ): VHService {
            vh.setImgCircle(id, url, onClickListener)
            return this
        }

        override fun setImgCircle(
            id: Int,
            imgRes: Int,
            onClickListener: ((view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit)?
        ): VHService {
            vh.setImgCircle(id, imgRes, onClickListener)
            return this
        }

        override fun bindImgCircle(
            context: Context,
            url: String,
            imageView: ImageView?
        ): VHService = this

        override fun bindImg(context: Context, url: String, imageView: ImageView?): VHService {
            return this
        }

        override fun bindImgRoundRect(
            context: Context,
            url: String,
            radius: Float,
            imageView: ImageView?
        ): VHService = this

        override fun setOnClick(
            onClickListener: (view: View, vh: org.quick.viewHolder.ViewHolder) -> Unit,
            vararg ids: Int
        ): VHService {
            vh.setOnClick(onClickListener, *ids)
            return this
        }

        override fun setProgress(id: Int, value: Int): VHService {
            vh.setProgress(id, value)
            return this
        }

        override fun setCheck(id: Int, isChecked: Boolean): VHService {
            vh.setCheck(id, isChecked)
            return this
        }

        override fun setBackgroundResource(id: Int, bgResId: Int): VHService {
            vh.setBackgroundResource(id, bgResId)
            return this
        }

        override fun setBackground(id: Int, background: Drawable): VHService {
            vh.setBackground(id, background)
            return this
        }


        override fun setBackgroundColor(id: Int, background: Int): VHService {
            vh.setBackgroundColor(id, background)
            return this
        }


        override fun setVisibility(visibility: Int, vararg resIds: Int): VHService {
            vh.setVisibility(visibility, *resIds)
            return this
        }


        override fun getTextView(id: Int): TextView? = vh.getTextView(id)

        override fun getButton(id: Int): Button? = vh.getButton(id)

        override fun getImageView(id: Int): ImageView? = vh.getImageView(id)

        override fun getLinearLayout(id: Int): LinearLayout? = vh.getLinearLayout(id)

        override fun getRelativeLayout(id: Int): RelativeLayout? = vh.getRelativeLayout(id)

        override fun getFramLayout(id: Int): FrameLayout? = vh.getFramLayout(id)

        override fun getCheckBox(id: Int): CheckBox? = vh.getCheckBox(id)

        override fun getEditText(id: Int): EditText? = vh.getEditText(id)

    }


    class VH(itemView: View) : org.quick.viewHolder.ViewHolder(itemView) {
        private lateinit var viewHolder: VHService
        fun bindViewHolder(viewHolder: VHService) {
            this.viewHolder = viewHolder
        }

        override fun bindImg(context: Context, url: String, imageView: ImageView?): VHService {
            return viewHolder.bindImg(context, url, imageView)
        }

        override fun bindImgCircle(
            context: Context,
            url: String,
            imageView: ImageView?
        ): VHService {
            return viewHolder.bindImgCircle(context, url, imageView)
        }

        override fun bindImgRoundRect(
            context: Context,
            url: String,
            radius: Float,
            imageView: ImageView?
        ): VHService {
            return viewHolder.bindImgRoundRect(context, url, radius, imageView)
        }
    }
}
