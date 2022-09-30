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
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.hidroh.materialistic.AppUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import androidx.recyclerview.widget.LinearSmoothScroller
import android.graphics.PointF

/**
 * Light extension to [LinearLayoutManager] that overrides smooth scroller to
 * always snap to start
 */
class SnappyLinearLayoutManager(context: Context?, preload: Boolean) :
    LinearLayoutManager(context) {
    private val mExtraSpace: Int

    init {
        mExtraSpace = if (preload) AppUtils.getDisplayHeight(context) else 0
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView, state: RecyclerView.State, position: Int
    ) {
        val smoothScroller: SmoothScroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return this@SnappyLinearLayoutManager.computeScrollVectorForPosition(targetPosition)
            }

            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START // override base class behavior
            }
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    @Deprecated("Deprecated in Java")
    override fun getExtraLayoutSpace(state: RecyclerView.State): Int {
        return if (mExtraSpace == 0) super.getExtraLayoutSpace(state) else mExtraSpace
    }
}