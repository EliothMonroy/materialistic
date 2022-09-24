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
package io.github.hidroh.materialistic.widget

import android.content.Context
import android.util.AttributeSet
import kotlin.jvm.JvmOverloads
import androidx.appcompat.widget.AppCompatTextView
import io.github.hidroh.materialistic.Application

class TextView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : AppCompatTextView(
    context!!, attrs, defStyleAttr
) {
    init {
        if (!isInEditMode) {
            typeface = Application.TYPE_FACE
        }
    }
}