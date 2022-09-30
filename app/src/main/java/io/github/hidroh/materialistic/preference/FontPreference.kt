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
import android.text.TextUtils
import android.util.AttributeSet
import kotlin.jvm.JvmOverloads
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.hidroh.materialistic.Application
import io.github.hidroh.materialistic.R
import io.github.hidroh.materialistic.FontCache

class FontPreference @JvmOverloads constructor(
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
        textView.typeface = FontCache.getInstance()[context, mEntryValues[position]]
        textView.text = mEntries[position]
    }

    override fun persistString(value: String): Boolean {
        if (TextUtils.equals(key, context.getString(R.string.pref_font))) {
            Application.TYPE_FACE = FontCache.getInstance()[context, value]
        }
        return super.persistString(value)
    }
}