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
import kotlin.jvm.JvmOverloads
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import io.github.hidroh.materialistic.R
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.TextView
import io.github.hidroh.materialistic.AppUtils
import androidx.core.content.ContextCompat

class ThemeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.theme_view, this, true)
        val ta = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.theme))
        val wrapper = ContextThemeWrapper(context, ta.getResourceId(0, R.style.AppTheme))
        ta.recycle()
        val cardBackgroundColor = AppUtils.getThemedResId(wrapper, R.attr.colorCardBackground)
        val textColor = AppUtils.getThemedResId(wrapper, android.R.attr.textColorTertiary)
        setCardBackgroundColor(ContextCompat.getColor(wrapper, cardBackgroundColor))
        (findViewById<View>(R.id.content) as TextView).setTextColor(
            ContextCompat.getColor(
                wrapper,
                textColor
            )
        )
    }
}