package com.app.ia.driver.spanly.internal.helpers

import android.text.Spannable
import android.text.SpannableString

internal fun span(charSequence: CharSequence, o: Any) = (if (charSequence is String) SpannableString(charSequence) else charSequence as? SpannableString
    ?: SpannableString("")).apply {
    setSpan(o, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}