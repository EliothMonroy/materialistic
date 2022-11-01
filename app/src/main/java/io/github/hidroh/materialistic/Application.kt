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
import io.github.hidroh.materialistic.AdBlocker.init
import com.squareup.leakcanary.RefWatcher
import dagger.ObjectGraph
import androidx.appcompat.app.AppCompatDelegate
import io.github.hidroh.materialistic.data.AlgoliaClient
import com.squareup.leakcanary.LeakCanary
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import rx.schedulers.Schedulers
import android.graphics.Typeface

open class Application : android.app.Application(), Injectable {
    private var mRefWatcher: RefWatcher? = null
    final override var applicationGraph: ObjectGraph? = null
        private set

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        applicationGraph = ObjectGraph.create()
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(Preferences.Theme.getAutoDayNightMode(this))
        AlgoliaClient.sSortByTime = Preferences.isSortByRecent(this)
        mRefWatcher = LeakCanary.install(this)
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyFlashScreen()
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
        Preferences.migrate(this)
        TYPE_FACE = FontCache.getInstance()[this, Preferences.Theme.getTypeface(this)]
        AppUtils.registerAccountsUpdatedListener(this)
        init(this, Schedulers.io())
    }

    override fun inject(`object`: Any?) {
        applicationGraph!!.inject(`object`)
    }

    companion object {
        var TYPE_FACE: Typeface? = null

        @JvmStatic
        fun getRefWatcher(context: Context): RefWatcher? {
            val application = context.applicationContext as Application
            return application.mRefWatcher
        }
    }
}