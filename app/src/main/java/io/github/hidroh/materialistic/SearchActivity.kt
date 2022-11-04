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

import android.app.SearchManager
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import io.github.hidroh.materialistic.data.AlgoliaClient
import io.github.hidroh.materialistic.data.HackerNewsClient
import io.github.hidroh.materialistic.data.SearchRecentSuggestionsProvider

class SearchActivity : BaseListActivity() {
    private var mQuery: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.hasExtra(SearchManager.QUERY)) {
            mQuery = intent.getStringExtra(SearchManager.QUERY)
        }
        super.onCreate(savedInstanceState)
        if (!TextUtils.isEmpty(mQuery)) {
            supportActionBar.subtitle = mQuery
            val suggestions: SearchRecentSuggestions = object : SearchRecentSuggestions(
                this,
                SearchRecentSuggestionsProvider.PROVIDER_AUTHORITY,
                SearchRecentSuggestionsProvider.MODE
            ) {
                override fun saveRecentQuery(queryString: String, line2: String) {
                    truncateHistory(contentResolver, MAX_RECENT_SUGGESTIONS - 1)
                    super.saveRecentQuery(queryString, line2)
                }
            }
            suggestions.saveRecentQuery(mQuery, null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_sort, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(if (AlgoliaClient.sSortByTime) R.id.menu_sort_recent else R.id.menu_sort_popular).isChecked =
            true
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.groupId == R.id.menu_sort_group) {
            item.isChecked = true
            sort(item.itemId == R.id.menu_sort_recent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getDefaultTitle(): String {
        return getString(R.string.title_activity_search)
    }

    override fun instantiateListFragment(): Fragment {
        val args = Bundle()
        args.putString(ListFragment.EXTRA_FILTER, mQuery)
        if (TextUtils.isEmpty(mQuery)) {
            args.putString(ListFragment.EXTRA_ITEM_MANAGER, HackerNewsClient::class.java.name)
        } else {
            args.putString(ListFragment.EXTRA_ITEM_MANAGER, AlgoliaClient::class.java.name)
        }
        return Fragment.instantiate(this, ListFragment::class.java.name, args)
    }

    private fun sort(byTime: Boolean) {
        if (AlgoliaClient.sSortByTime == byTime) {
            return
        }
        AlgoliaClient.sSortByTime = byTime
        Preferences.setSortByRecent(this, byTime)
        val listFragment = supportFragmentManager
            .findFragmentByTag(LIST_FRAGMENT_TAG) as ListFragment?
        listFragment?.filter(mQuery)
    }

    companion object {
        private const val MAX_RECENT_SUGGESTIONS = 10
    }
}