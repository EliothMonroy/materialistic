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
package io.github.hidroh.materialistic.preference

import android.content.Context
import android.util.AttributeSet
import kotlin.jvm.JvmOverloads
import android.view.LayoutInflater
import android.view.ViewGroup
import io.github.hidroh.materialistic.R
import io.github.hidroh.materialistic.AppUtils
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import io.github.hidroh.materialistic.Preferences

class FontSizePreference @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : SpinnerPreference(context, attrs, defStyleAttr) {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(getContext())

    override fun createDropDownView(position: Int, parent: ViewGroup): View {
        return mLayoutInflater.inflate(R.layout.support_simple_spinner_dropdown_item, parent, false)
    }

    override fun bindDropDownView(position: Int, view: View) {
        val textView = view.findViewById<TextView>(android.R.id.text1)
        val textSize = AppUtils.getDimension(
            context,
            Preferences.Theme.resolveTextSize(mEntryValues[position]),
            R.attr.contentTextSize
        )
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        textView.text = mEntries[position]
    }
}