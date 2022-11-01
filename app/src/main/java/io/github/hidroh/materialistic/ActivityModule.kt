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

import dagger.Module
import io.github.hidroh.materialistic.data.ItemSyncService
import io.github.hidroh.materialistic.appwidget.WidgetService
import io.github.hidroh.materialistic.data.ItemSyncJobService
import dagger.Provides
import javax.inject.Singleton
import android.accounts.AccountManager
import android.content.Context

@Module(
    injects = [ItemSyncService::class, WidgetService::class, ItemSyncJobService::class],
    library = true,
    includes = [DataModule::class]
)
class ActivityModule(private val mContext: Context) {
    @Provides
    @Singleton
    fun provideContext(): Context {
        return mContext
    }

    @Provides
    @Singleton
    fun provideAccountManager(context: Context?): AccountManager {
        return AccountManager.get(context)
    }

    companion object {
        const val ALGOLIA = "algolia"
        const val POPULAR = "popular"
        const val HN = "hn"
    }
}