package com.app.ia.driver.spanly.internal.spans

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan


class FontSpan(private val font: Typeface) : MetricAffectingSpan() {

    override fun updateMeasureState(textPaint: TextPaint) = update(textPaint)

    override fun updateDrawState(textPaint: TextPaint) = update(textPaint)

    private fun update(textPaint: TextPaint) {
        textPaint.apply {
            val old = typeface
            val oldStyle = old?.style ?: 0

            // Keep the style set before
            val font = Typeface.create(font, oldStyle)
            typeface = font
        }
    }
}