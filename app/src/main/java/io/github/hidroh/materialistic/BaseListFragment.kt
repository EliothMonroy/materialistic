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

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import io.github.hidroh.materialistic.KeyDelegate.RecyclerViewHelper
import io.github.hidroh.materialistic.widget.ListRecyclerViewAdapter
import io.github.hidroh.materialistic.widget.SnappyLinearLayoutManager
import javax.inject.Inject

internal abstract class BaseListFragment : BaseFragment(), Scrollable {
    @JvmField
    @Inject
    var mCustomTabsDelegate: CustomTabsDelegate? = null
    private var mScrollableHelper: RecyclerViewHelper? = null

    @JvmField
    protected var mRecyclerView: RecyclerView? = null
    private val mPreferenceObservable = Preferences.Observable()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mPreferenceObservable.subscribe(
            context,
            { key: Int, contextChanged: Boolean -> onPreferenceChanged(key, contextChanged) },
            R.string.pref_font,
            R.string.pref_text_size,
            R.string.pref_list_item_view
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView!!.layoutManager = SnappyLinearLayoutManager(activity, false)
        val verticalMargin = resources
            .getDimensionPixelSize(R.dimen.cardview_vertical_margin)
        val horizontalMargin = resources
            .getDimensionPixelSize(R.dimen.cardview_horizontal_margin)
        val divider = resources.getDimensionPixelSize(R.dimen.divider)
        mRecyclerView!!.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView,
                state: RecyclerView.State
            ) {
                if (adapter.isCardViewEnabled) {
                    outRect[horizontalMargin, verticalMargin, horizontalMargin] = 0
                } else {
                    outRect[0, 0, 0] = divider
                }
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            adapter.restoreState(savedInstanceState.getBundle(STATE_ADAPTER))
        }
        adapter.isCardViewEnabled =
            Preferences.isListItemCardView(activity)
        adapter.setCustomTabsDelegate(mCustomTabsDelegate)
        mRecyclerView!!.adapter = adapter
        mScrollableHelper = RecyclerViewHelper(
            mRecyclerView,
            RecyclerViewHelper.SCROLL_PAGE
        )
    }

    override fun createOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_list) {
            showPreferences()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showPreferences() {
        val args = Bundle()
        args.putInt(PopupSettingsFragment.EXTRA_TITLE, R.string.list_display_options)
        args.putInt(PopupSettingsFragment.EXTRA_SUMMARY, R.string.pull_up_hint)
        args.putIntArray(
            PopupSettingsFragment.EXTRA_XML_PREFERENCES, intArrayOf(
                R.xml.preferences_font,
                R.xml.preferences_list
            )
        )
        (instantiate(
            requireActivity(),
            PopupSettingsFragment::class.java.name, args
        ) as DialogFragment)
            .show(requireFragmentManager(), PopupSettingsFragment::class.java.name)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(STATE_ADAPTER, adapter.saveState())
    }

    override fun onDetach() {
        super.onDetach()
        mPreferenceObservable.unsubscribe(activity)
        mRecyclerView!!.adapter = null // force adapter detach
    }

    override fun scrollToTop() {
        mScrollableHelper!!.scrollToTop()
    }

    override fun scrollToNext(): Boolean {
        return mScrollableHelper!!.scrollToNext()
    }

    override fun scrollToPrevious(): Boolean {
        return mScrollableHelper!!.scrollToPrevious()
    }

    private fun onPreferenceChanged(key: Int, contextChanged: Boolean) {
        if (contextChanged) {
            mRecyclerView!!.adapter = adapter
        } else if (key == R.string.pref_list_item_view) {
            adapter.isCardViewEnabled =
                Preferences.isListItemCardView(activity)
        }
    }

    protected abstract val adapter: ListRecyclerViewAdapter<*, *>

    companion object {
        private const val STATE_ADAPTER = "state:adapter"
    }
}