/*
 * Copyright (c) 2015 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.hidroh.materialistic.widget.preference

import android.content.Context
import android.widget.ScrollView
import android.view.LayoutInflater
import io.github.hidroh.materialistic.R
import android.text.SpannableString
import android.text.style.SuperscriptSpan
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import android.text.SpannableStringBuilder
import android.text.Spannable
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import io.github.hidroh.materialistic.widget.AsteriskSpan

class HelpListView(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs) {
    init {
        addView(LayoutInflater.from(context).inflate(R.layout.include_help_list_view, this, false))
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        (findViewById<View>(R.id.item_new).findViewById<View>(R.id.rank) as TextView)
            .append(makeAsteriskSpan())
        val spannable = SpannableString("+5")
        spannable.setSpan(
            SuperscriptSpan(), 0, spannable.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            RelativeSizeSpan(0.6f), 0, spannable.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(context, R.color.greenA700)
            ), 0, spannable.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        (findViewById<View>(R.id.item_promoted).findViewById<View>(R.id.rank) as TextView).append(
            spannable
        )
        val comments =
            findViewById<View>(R.id.item_new_comments).findViewById<TextView>(R.id.comment)
        val sb = SpannableStringBuilder("46")
        sb.append(makeAsteriskSpan())
        comments.text = sb
    }

    private fun makeAsteriskSpan(): Spannable {
        val sb = SpannableString("*")
        sb.setSpan(
            AsteriskSpan(context), sb.length - 1, sb.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return sb
    }
}