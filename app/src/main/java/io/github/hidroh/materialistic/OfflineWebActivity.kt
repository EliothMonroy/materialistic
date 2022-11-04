/*
 * Copyright (c) 2016 Ha Duy Trung
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

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import io.github.hidroh.materialistic.widget.AdBlockWebViewClient
import io.github.hidroh.materialistic.widget.CacheableWebView.ArchiveClient

class OfflineWebActivity : InjectableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(EXTRA_URL)
        if (TextUtils.isEmpty(url)) {
            finish()
            return
        }
        title = url!!
        setContentView(R.layout.activity_offline_web)
        val scrollView = findViewById<NestedScrollView>(R.id.nested_scroll_view)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setOnClickListener { scrollView.smoothScrollTo(0, 0) }
        setSupportActionBar(toolbar)
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_HOME or
                ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
        supportActionBar!!.setSubtitle(R.string.offline)
        val progressBar = findViewById<ProgressBar>(R.id.progress)
        val webView = findViewById<WebView>(R.id.web_view)
        webView.setBackgroundColor(Color.TRANSPARENT)
        webView.webViewClient = object : AdBlockWebViewClient(Preferences.adBlockEnabled(this)) {
            override fun onPageFinished(view: WebView, url: String) {
                title = view.title!!
            }
        }
        webView.webChromeClient = object : ArchiveClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.visibility = View.VISIBLE
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                    webView.setBackgroundColor(Color.WHITE)
                    webView.visibility = View.VISIBLE
                }
            }
        }
        AppUtils.toggleWebViewZoom(webView.settings, true)
        webView.loadUrl(url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmField
        val EXTRA_URL = OfflineWebActivity::class.java.name + ".EXTRA_URL"
    }
}