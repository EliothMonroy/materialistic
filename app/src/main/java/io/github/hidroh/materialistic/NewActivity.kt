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

import android.content.Intent
import io.github.hidroh.materialistic.data.ItemManager

class NewActivity : BaseStoriesActivity() {
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra(EXTRA_REFRESH, false)) {
            // triggered by new submission from user, refresh list
            val listFragment = supportFragmentManager
                .findFragmentByTag(LIST_FRAGMENT_TAG) as ListFragment?
            listFragment?.filter(fetchMode)
        }
    }

    override fun getDefaultTitle(): String {
        return getString(R.string.title_activity_new)
    }

    override val fetchMode: String
        get() = ItemManager.NEW_FETCH_MODE

    override fun getItemCacheMode(): Int {
        return ItemManager.MODE_NETWORK
    }

    companion object {
        @JvmField
        val EXTRA_REFRESH = NewActivity::class.java.name + ".EXTRA_REFRESH"
    }
}