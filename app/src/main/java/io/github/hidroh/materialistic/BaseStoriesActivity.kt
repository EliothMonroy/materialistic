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

import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.format.DateUtils
import androidx.fragment.app.Fragment
import io.github.hidroh.materialistic.ListFragment.RefreshCallback
import io.github.hidroh.materialistic.annotation.Synthetic
import io.github.hidroh.materialistic.data.HackerNewsClient
import io.github.hidroh.materialistic.data.ItemManager.FetchMode

abstract class BaseStoriesActivity : BaseListActivity(), RefreshCallback {
    @Synthetic
    var mLastUpdated: Long? = null
    private val mLastUpdateTask: Runnable = object : Runnable {
        override fun run() {
            if (mLastUpdated == null) {
                return
            }
            if (AppUtils.hasConnection(this@BaseStoriesActivity)) {
                supportActionBar.subtitle = getString(
                    R.string.last_updated,
                    DateUtils.getRelativeTimeSpanString(
                        mLastUpdated!!,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_ALL
                    )
                )
                mHandler.postAtTime(this, SystemClock.uptimeMillis() + DateUtils.MINUTE_IN_MILLIS)
            } else {
                supportActionBar.setSubtitle(R.string.offline)
            }
        }
    }

    @Synthetic
    val mHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            mLastUpdated = savedInstanceState.getLong(STATE_LAST_UPDATED)
        }
    }

    override fun onResume() {
        super.onResume()
        mHandler.removeCallbacks(mLastUpdateTask)
        mHandler.post(mLastUpdateTask)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mLastUpdateTask)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mLastUpdated != null) {
            outState.putLong(STATE_LAST_UPDATED, mLastUpdated!!)
        }
    }

    override fun onRefreshed() {
        onItemSelected(null)
        mLastUpdated = System.currentTimeMillis()
        mHandler.removeCallbacks(mLastUpdateTask)
        mHandler.post(mLastUpdateTask)
    }

    @get:FetchMode
    protected abstract val fetchMode: String
    override fun instantiateListFragment(): Fragment {
        val args = Bundle()
        args.putString(ListFragment.EXTRA_ITEM_MANAGER, HackerNewsClient::class.java.name)
        args.putString(ListFragment.EXTRA_FILTER, fetchMode)
        return Fragment.instantiate(this, ListFragment::class.java.name, args)
    }

    companion object {
        private const val STATE_LAST_UPDATED = "state:lastUpdated"
    }
}