package io.github.hidroh.materialistic

import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.hidroh.materialistic.data.Item
import io.github.hidroh.materialistic.data.ItemManager
import io.github.hidroh.materialistic.data.ItemManager.CacheMode
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

class StoryListViewModel : ViewModel() {
    private var mItemManager: ItemManager? = null
    private var mIoThreadScheduler: Scheduler? = null
    private var mItems // first = last updated, second = current
            : MutableLiveData<Pair<Array<Item>?, Array<Item>>?>? = null

    fun inject(itemManager: ItemManager?, ioThreadScheduler: Scheduler?) {
        mItemManager = itemManager
        mIoThreadScheduler = ioThreadScheduler
    }

    fun getStories(
        filter: String?,
        @CacheMode cacheMode: Int
    ): LiveData<Pair<Array<Item>?, Array<Item>>?> {
        if (mItems == null) {
            mItems = MutableLiveData()
            Observable.fromCallable { mItemManager!!.getStories(filter, cacheMode) }
                .subscribeOn(mIoThreadScheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { items: Array<Item> -> setItems(items) }
        }
        return mItems!!
    }

    fun refreshStories(filter: String?, @CacheMode cacheMode: Int) {
        if (mItems == null || mItems!!.value == null) {
            return
        }
        Observable.fromCallable { mItemManager!!.getStories(filter, cacheMode) }
            .subscribeOn(mIoThreadScheduler)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { items: Array<Item> -> setItems(items) }
    }

    fun setItems(items: Array<Item>) {
        mItems!!.value =
            Pair.create(if (mItems!!.value != null) mItems!!.value!!.second else null, items)
    }
}