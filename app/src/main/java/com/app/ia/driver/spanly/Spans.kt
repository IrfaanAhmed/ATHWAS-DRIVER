package com.app.ia.driver.spanly

import android.graphics.Typeface
import android.text.style.*
import android.view.View
import com.app.ia.driver.spanly.internal.spans.CustomClickableSpan
import com.app.ia.driver.spanly.internal.spans.FontSpan


fun bold() = StyleSpan(Typeface.BOLD)
fun italic() = StyleSpan(Typeface.ITALIC)
fun underline() = UnderlineSpan()
fun strike() = StrikethroughSpan()
fun sup() = SuperscriptSpan()
fun sub() = SubscriptSpan()
fun background(color: Int) = BackgroundColorSpan(color)
fun color(color: Int) = ForegroundColorSpan(color)
fun size(size: Float) = RelativeSizeSpan(size)
fun font(typeface: Typeface) = FontSpan(typeface)
fun clickable(listener: View.OnClickListener, isUnderlineText: Boolean = false) = CustomClickableSpan(listener, isUnderlineText)
