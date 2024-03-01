package com.heytap.browser.core.common.image

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.PixelFormat.TRANSLUCENT
import android.graphics.drawable.Drawable
import androidx.annotation.RequiresApi


/**
 * Copyright 2010-2020 OPPO Mobile Comm Corp., Ltd.
 * All rights reserved.
 * Description : FusionViewDemo
 * Author      : W9052204
 * Date        : 2022/6/24
 */

@RequiresApi(21)
class RoundRectDrawable(radius: Float) :
    Drawable() {
    private var mRadius: Float
    private val mPaint: Paint
    private val mBoundsF: RectF
    private val mBoundsI: Rect
    var padding = 0f
        private set
    private var mInsetForPadding = false
    private var mInsetForRadius = true
    private var mTint: ColorStateList? = null

    fun setPadding(padding: Float, insetForPadding: Boolean, insetForRadius: Boolean) {
        if (padding != this.padding || mInsetForPadding != insetForPadding || mInsetForRadius != insetForRadius) {
            this.padding = padding
            mInsetForPadding = insetForPadding
            mInsetForRadius = insetForRadius
            updateBounds(null as Rect?)
            invalidateSelf()
        }
    }

    override fun draw(canvas: Canvas) {
        val path: Path = RoundRectUtil.getInstance().getPath(
            mBoundsF,
            mRadius
        )
        canvas.drawPath(path, mPaint)
    }

    private fun updateBounds(bounds: Rect?) {
        var bounds = bounds
        if (bounds == null) {
            bounds = this.bounds
        }
        mBoundsF[bounds.left.toFloat(), bounds.top.toFloat(), bounds.right.toFloat()] =
            bounds.bottom.toFloat()
        mBoundsI.set(bounds)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        updateBounds(bounds)
    }

    override fun getOutline(outline: Outline) {
        outline.setRoundRect(mBoundsI, mRadius)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return TRANSLUCENT
    }

    var radius: Float
        get() = mRadius
        set(radius) {
            if (radius != mRadius) {
                mRadius = radius
                updateBounds(null as Rect?)
                invalidateSelf()
            }
        }


    override fun isStateful(): Boolean {
        return mTint != null && mTint!!.isStateful || super.isStateful()
    }


    init {
        mRadius = radius
        mPaint = Paint(5)
        mBoundsF = RectF()
        mBoundsI = Rect()
        mPaint.color = Color.parseColor("#01FFFFFF")
    }
}
