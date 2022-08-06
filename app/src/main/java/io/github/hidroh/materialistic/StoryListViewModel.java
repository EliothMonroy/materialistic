package io.github.hidroh.materialistic;

import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.github.hidroh.materialistic.data.Item;
import io.github.hidroh.materialistic.data.ItemManager;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;

public class StoryListViewModel extends ViewModel {
    private ItemManager mItemManager;
    private Scheduler mIoThreadScheduler;
    private MutableLiveData<Pair<Item[], Item[]>> mItems; // first = last updated, second = current

    public void inject(ItemManager itemManager, Scheduler ioThreadScheduler) {
        mItemManager = itemManager;
        mIoThreadScheduler = ioThreadScheduler;
    }

    public LiveData<Pair<Item[], Item[]>> getStories(String filter, @ItemManager.CacheMode int cacheMode) {
        if (mItems == null) {
            mItems = new MutableLiveData<>();
            Observable.fromCallable(() -> mItemManager.getStories(filter, cacheMode))
                    .subscribeOn(mIoThreadScheduler)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setItems);
        }
        return mItems;
    }

    public void refreshStories(String filter, @ItemManager.CacheMode int cacheMode) {
        if (mItems == null || mItems.getValue() == null) {
            return;
        }
        Observable.fromCallable(() -> mItemManager.getStories(filter, cacheMode))
                .subscribeOn(mIoThreadScheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setItems);

    }

    void setItems(Item[] items) {
        mItems.setValue(Pair.create(mItems.getValue() != null ? mItems.getValue().second : null, items));
    }
}
