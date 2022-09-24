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
import android.util.AttributeSet
import android.widget.ScrollView
import android.view.LayoutInflater
import io.github.hidroh.materialistic.R
import com.google.android.material.tabs.TabLayout
import io.github.hidroh.materialistic.Preferences
import io.github.hidroh.materialistic.Preferences.StoryViewMode

class HelpLazyLoadView(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs) {
    init {
        addView(LayoutInflater.from(context).inflate(R.layout.include_help_lazy_load, this, false))
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText(R.string.comments))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.article))
        val defaultTab: Int = when (Preferences.getDefaultStoryView(context)) {
            StoryViewMode.Comment -> 0
            StoryViewMode.Article, StoryViewMode.Readability -> 1
            else -> 0
        }
        tabLayout.getTabAt(defaultTab)!!.select()
    }
}