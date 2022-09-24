/*
 * Copyright (c) 2016 Ha Duy Trung
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
package io.github.hidroh.materialistic.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import kotlin.jvm.JvmOverloads

class ViewPager @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    ViewPager(
        context!!, attrs
    ) {
    private var mSwipeEnabled = true
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return mSwipeEnabled && super.onInterceptTouchEvent(ev)
    }

    fun setSwipeEnabled(enabled: Boolean) {
        mSwipeEnabled = enabled
    }
}