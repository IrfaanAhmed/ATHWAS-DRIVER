package com.app.ia.driver.spanly.internal.spans

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View


class CustomClickableSpan(private val listener: View.OnClickListener, private val isUnderlineText: Boolean) : ClickableSpan() {

    override fun onClick(widget: View) {
        listener.onClick(widget)
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = isUnderlineText
    }
}