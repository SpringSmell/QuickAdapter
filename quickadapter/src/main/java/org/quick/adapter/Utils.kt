package org.quick.adapter

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager

object Utils{
    fun getSystemAttrValue(context: Context, attrResId: Int): Float {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return getSystemAttrTypeValue(context, attrResId).getDimension(outMetrics)
    }

    fun getSystemAttrTypeValue(context: Context, attrResId: Int): TypedValue {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrResId, typedValue, true)
        return typedValue
    }
}