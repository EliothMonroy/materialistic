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
package io.github.hidroh.materialistic.widget

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import io.github.hidroh.materialistic.AdBlocker.createEmptyResource
import io.github.hidroh.materialistic.AdBlocker.isAd

open class AdBlockWebViewClient(private val mAdBlockEnabled: Boolean) : WebViewClient() {
    private val mLoadedUrls: MutableMap<String, Boolean> = HashMap()

    @Deprecated("Deprecated in Java")
    override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
        if (!mAdBlockEnabled) {
            return super.shouldInterceptRequest(view, url)
        }
        val ad: Boolean
        if (!mLoadedUrls.containsKey(url)) {
            ad = isAd(url)
            mLoadedUrls[url] = ad
        } else {
            ad = mLoadedUrls[url]!!
        }
        return if (ad) createEmptyResource() else super.shouldInterceptRequest(view, url)
    }

    override fun shouldInterceptRequest(
        view: WebView, request: WebResourceRequest
    ): WebResourceResponse? {
        if (!mAdBlockEnabled) {
            return super.shouldInterceptRequest(view, request)
        }
        val ad: Boolean
        val url = request.url.toString()
        if (!mLoadedUrls.containsKey(url)) {
            ad = isAd(url)
            mLoadedUrls[url] = ad
        } else {
            ad = mLoadedUrls[url]!!
        }
        return if (ad) createEmptyResource() else super.shouldInterceptRequest(view, request)
    }
}