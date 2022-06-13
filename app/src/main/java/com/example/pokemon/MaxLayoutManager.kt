package com.example.pokemon

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MaxLayoutManager : LinearLayoutManager {
    private var maxCount = -1

    constructor(context: Context) : super(context) {}
    constructor(
        context: Context,
        orientation: Int,
        reverseLayout: Boolean
    ) : super(context, orientation, reverseLayout) {
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    fun setMaxCount(maxCount: Int) {
        this.maxCount = maxCount
    }

    override fun setMeasuredDimension(widthSize: Int, heightSize: Int) {
        val maxHeight = getMaxHeight()
        if (maxHeight > 0 && maxHeight < heightSize) {
            super.setMeasuredDimension(widthSize, maxHeight)
        } else {
            super.setMeasuredDimension(widthSize, heightSize)
        }
    }


    private fun getMaxHeight(): Int {
        if (childCount == 0 || maxCount <= 0) {
            return 0
        }
        val child = getChildAt(0)
        var height = child!!.height
        val lp = child.layoutParams as RecyclerView.LayoutParams
        height += lp.topMargin + lp.bottomMargin
        return height * maxCount + paddingBottom + paddingTop
    }
}