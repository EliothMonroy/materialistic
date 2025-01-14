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

import android.view.View
import android.widget.TextView
import io.github.hidroh.materialistic.R

class ToggleItemViewHolder : ItemRecyclerViewAdapter.ItemViewHolder {
    @JvmField
    var mToggleButton: View? = null

    @JvmField
    var mToggle: TextView? = null

    @JvmField
    var mLevel: View? = null

    internal constructor(itemView: View) : super(itemView) {
        mToggleButton = itemView.findViewById(R.id.button_toggle)
        mToggle = itemView.findViewById(R.id.toggle)
        mLevel = itemView.findViewById(R.id.level)
    }

    internal constructor(itemView: View?, payload: Any?) : super(itemView, payload) {}
}