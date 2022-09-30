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
import androidx.cardview.widget.CardView

class FlatCardView : CardView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    )

    fun flatten() {
        radius = 0f
        useCompatPadding = false
        if (layoutParams is MarginLayoutParams) {
            val marginLayoutParams = layoutParams as MarginLayoutParams
            marginLayoutParams.leftMargin = contentPaddingLeft - paddingLeft
            marginLayoutParams.rightMargin = contentPaddingRight - paddingRight
            marginLayoutParams.topMargin = contentPaddingTop - paddingTop
            marginLayoutParams.bottomMargin = contentPaddingBottom - paddingBottom
        }
    }
}