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
package io.github.hidroh.materialistic

import android.R.attr
import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Helper to tint menu items for activities and fragments
 */
class MenuTintDelegate {
    private var mTextColorPrimary = 0

    /**
     * Callback that should be triggered after activity has been created
     *
     * @param context activity context
     */
    fun onActivityCreated(context: Context?) {
        mTextColorPrimary = ContextCompat.getColor(
            context!!,
            AppUtils.getThemedResId(context, attr.textColorPrimary)
        )
    }

    /**
     * Callback that should be triggered after menu has been inflated
     *
     * @param menu inflated menu
     */
    fun onOptionsMenuCreated(menu: Menu) {
        for (i in 0 until menu.size()) {
            var drawable = menu.getItem(i).icon ?: continue
            drawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(drawable, mTextColorPrimary)
        }
    }

    fun setIcon(item: MenuItem, @DrawableRes icon: Int) {
        item.setIcon(icon)
        var drawable = item.icon
        drawable = DrawableCompat.wrap(drawable!!)
        DrawableCompat.setTint(drawable, mTextColorPrimary)
    }
}