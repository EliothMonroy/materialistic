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
package io.github.hidroh.materialistic.accounts

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.widget.Toast
import io.github.hidroh.materialistic.AppUtils
import io.github.hidroh.materialistic.R
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request
import okhttp3.Request.Builder
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.util.regex.Pattern
import javax.inject.Inject

class UserServicesClient @Inject constructor(callFactory: Call.Factory, ioScheduler: Scheduler) :
    UserServices {
    private val mCallFactory: Call.Factory
    private val mIoScheduler: Scheduler

    init {
        mCallFactory = callFactory
        mIoScheduler = ioScheduler
    }

    override fun login(
        username: String,
        password: String,
        createAccount: Boolean,
        callback: UserServices.Callback
    ) {
        execute(postLogin(username, password, createAccount))
            .flatMap { response: Response ->
                if (response.code == HttpURLConnection.HTTP_OK) {
                    return@flatMap Observable.error<Boolean>(
                        UserServices.Exception(
                            parseLoginError(
                                response
                            )
                        )
                    )
                }
                Observable.just(response.code == HttpURLConnection.HTTP_MOVED_TEMP)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ successful: Boolean? ->
                callback.onDone(
                    successful!!
                )
            }) { throwable: Throwable? -> callback.onError(throwable) }
    }

    override fun voteUp(
        context: Context,
        itemId: String,
        callback: UserServices.Callback
    ): Boolean {
        val credentials = AppUtils.getCredentials(context) ?: return false
        Toast.makeText(context, R.string.sending, Toast.LENGTH_SHORT).show()
        execute(postVote(credentials.first, credentials.second, itemId))
            .map { response: Response -> response.code == HttpURLConnection.HTTP_MOVED_TEMP }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ successful: Boolean? ->
                callback.onDone(
                    successful!!
                )
            }) { throwable: Throwable? -> callback.onError(throwable) }
        return true
    }

    override fun reply(
        context: Context,
        parentId: String,
        text: String,
        callback: UserServices.Callback
    ) {
        val credentials = AppUtils.getCredentials(context)
        if (credentials == null) {
            callback.onDone(false)
            return
        }
        execute(postReply(parentId, text, credentials.first, credentials.second))
            .map { response: Response -> response.code == HttpURLConnection.HTTP_MOVED_TEMP }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ successful: Boolean? ->
                callback.onDone(
                    successful!!
                )
            }) { throwable: Throwable? -> callback.onError(throwable) }
    }

    override fun submit(
        context: Context,
        title: String,
        content: String,
        isUrl: Boolean,
        callback: UserServices.Callback
    ) {
        val credentials = AppUtils.getCredentials(context)
        if (credentials == null) {
            callback.onDone(false)
            return
        }
        /*
          The flow:
          POST /submit with acc, pw
           if 302 to /login, considered failed
          POST /r with fnid, fnop, title, url or text
           if 302 to /newest, considered successful
           if 302 to /x, considered error, maybe duplicate or invalid input
           if 200 or anything else, considered error
         */
        // fetch submit page with given credentials
        execute(postSubmitForm(credentials.first, credentials.second))
            .flatMap { response: Response ->
                if (response.code != HttpURLConnection.HTTP_MOVED_TEMP) Observable.just(response) else Observable.error(
                    IOException()
                )
            }
            .flatMap<Array<String?>> { response: Response ->
                try {
                    response.use {
                        return@flatMap Observable.just<Array<String?>>(
                            arrayOf<String?>(
                                response.header(HEADER_SET_COOKIE),
                                response.body.string()
                            )
                        )
                    }
                } catch (e: IOException) {
                    return@flatMap Observable.error<Array<String>>(e)
                }
            }
            .map { array: Array<String?> ->
                array[1] = getInputValue(array[1], SUBMIT_PARAM_FNID)
                array
            }
            .flatMap(Function<Array<String?>, ObservableSource<out Array<String?>>> { array: Array<String?> ->
                if (!TextUtils.isEmpty(
                        array[1]
                    )
                ) Observable.just(array) else Observable.error<Array<String>>(IOException())
            })
            .flatMap { array: Array<String?> ->
                execute(
                    postSubmit(
                        title,
                        content,
                        isUrl,
                        array[0],
                        array[1]
                    )
                )
            }
            .flatMap { response: Response ->
                if (response.code == HttpURLConnection.HTTP_MOVED_TEMP) Observable.just(
                    Uri.parse(response.header(HEADER_LOCATION))
                ) else Observable.error(IOException())
            }
            .flatMap { uri: Uri ->
                if (TextUtils.equals(
                        uri.path,
                        DEFAULT_SUBMIT_REDIRECT
                    )
                ) Observable.just(true) else Observable.error(buildException(uri))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ successful: Boolean? ->
                callback.onDone(
                    successful!!
                )
            }) { throwable: Throwable? -> callback.onError(throwable) }
    }

    private fun postLogin(username: String, password: String, createAccount: Boolean): Request {
        val formBuilder: Builder = Builder()
            .add(LOGIN_PARAM_ACCT, username)
            .add(LOGIN_PARAM_PW, password)
            .add(LOGIN_PARAM_GOTO, DEFAULT_REDIRECT)
        if (createAccount) {
            formBuilder.add(LOGIN_PARAM_CREATING, CREATING_TRUE)
        }
        return Builder()
            .url(
                HttpUrl.parse(BASE_WEB_URL)
                    .newBuilder()
                    .addPathSegment(LOGIN_PATH)
                    .build()
            )
            .post(formBuilder.build())
            .build()
    }

    private fun postVote(username: String, password: String, itemId: String): Request? {
        return BASE_WEB_URL.toHttpUrlOrNull()
            ?.newBuilder()
            ?.addPathSegment(VOTE_PATH)?.let {
                Builder()
                .url(
                    it
                        .build()
                )
                .post(
                    Builder()
                        .add(LOGIN_PARAM_ACCT, username)
                        .add(LOGIN_PARAM_PW, password)
                        .add(VOTE_PARAM_ID, itemId)
                        .add(VOTE_PARAM_HOW, VOTE_DIR_UP)
                        .build()
                )
                .build()
            }
    }

    private fun postReply(
        parentId: String,
        text: String,
        username: String,
        password: String
    ): Request? {
        return BASE_WEB_URL.toHttpUrlOrNull()
            ?.newBuilder()
            ?.addPathSegment(COMMENT_PATH)?.let {
                Builder()
                .url(
                    it
                        .build()
                )
                .post(
                    Builder()
                        .add(LOGIN_PARAM_ACCT, username)
                        .add(LOGIN_PARAM_PW, password)
                        .add(COMMENT_PARAM_PARENT, parentId)
                        .add(COMMENT_PARAM_TEXT, text)
                        .build()
                )
                .build()
            }
    }

    private fun postSubmitForm(username: String, password: String): Request {
        return Builder()
            .url(
                HttpUrl.parse(BASE_WEB_URL)
                    .newBuilder()
                    .addPathSegment(SUBMIT_PATH)
                    .build()
            )
            .post(
                Builder()
                    .add(LOGIN_PARAM_ACCT, username)
                    .add(LOGIN_PARAM_PW, password)
                    .build()
            )
            .build()
    }

    private fun postSubmit(
        title: String,
        content: String,
        isUrl: Boolean,
        cookie: String?,
        fnid: String?
    ): Request? {
        val builder: Builder? = BASE_WEB_URL.toHttpUrlOrNull()
            ?.newBuilder()
            ?.addPathSegment(SUBMIT_POST_PATH)
            ?.build()?.let {
                Builder()
                .url(
                    it
                )
                .post(
                    Builder()
                        .add(SUBMIT_PARAM_FNID, fnid)
                        .add(SUBMIT_PARAM_FNOP, DEFAULT_FNOP)
                        .add(SUBMIT_PARAM_TITLE, title)
                        .add(if (isUrl) SUBMIT_PARAM_URL else SUBMIT_PARAM_TEXT, content)
                        .build()
                )
            }
        if (!TextUtils.isEmpty(cookie)) {
            builder?.addHeader(HEADER_COOKIE, cookie)
        }
        return builder?.build()
    }

    private fun execute(request: Request): Observable<Response> {
        return Observable.defer {
            try {
                return@defer Observable.just<Response>(mCallFactory.newCall(request).execute())
            } catch (e: IOException) {
                return@defer Observable.error<Response>(e)
            }
        }.subscribeOn(mIoScheduler)
    }

    private fun buildException(uri: Uri): Throwable {
        if (ITEM_PATH == uri.path) {
            val exception = UserServices.Exception(R.string.item_exist)
            val itemId = uri.getQueryParameter(ITEM_PARAM_ID)
            if (!TextUtils.isEmpty(itemId)) {
                exception.data = AppUtils.createItemUri(itemId!!)
            }
            return exception
        }
        return IOException()
    }

    private fun getInputValue(html: String?, name: String): String? {
        // extract <input ... >
        val matcherInput = Pattern.compile(REGEX_INPUT).matcher(html)
        while (matcherInput.find()) {
            val input = matcherInput.group()
            if (input.contains(name)) {
                // extract value="..."
                val matcher = Pattern.compile(REGEX_VALUE).matcher(input)
                return if (matcher.find()) matcher.group(1) else null // return first match if any
            }
        }
        return null
    }

    private fun parseLoginError(response: Response): String? {
        return try {
            val matcher = Pattern.compile(REGEX_CREATE_ERROR_BODY).matcher(response.body.string())
            if (matcher.find()) matcher.group(1)?.replace("\\n|\\r|\\t|\\s+".toRegex(), " ")
                ?.trim { it <= ' ' } else null
        } catch (e: IOException) {
            null
        }
    }

    companion object {
        private const val BASE_WEB_URL = "https://news.ycombinator.com"
        private const val LOGIN_PATH = "login"
        private const val VOTE_PATH = "vote"
        private const val COMMENT_PATH = "comment"
        private const val SUBMIT_PATH = "submit"
        private const val ITEM_PATH = "item"
        private const val SUBMIT_POST_PATH = "r"
        private const val LOGIN_PARAM_ACCT = "acct"
        private const val LOGIN_PARAM_PW = "pw"
        private const val LOGIN_PARAM_CREATING = "creating"
        private const val LOGIN_PARAM_GOTO = "goto"
        private const val ITEM_PARAM_ID = "id"
        private const val VOTE_PARAM_ID = "id"
        private const val VOTE_PARAM_HOW = "how"
        private const val COMMENT_PARAM_PARENT = "parent"
        private const val COMMENT_PARAM_TEXT = "text"
        private const val SUBMIT_PARAM_TITLE = "title"
        private const val SUBMIT_PARAM_URL = "url"
        private const val SUBMIT_PARAM_TEXT = "text"
        private const val SUBMIT_PARAM_FNID = "fnid"
        private const val SUBMIT_PARAM_FNOP = "fnop"
        private const val VOTE_DIR_UP = "up"
        private const val DEFAULT_REDIRECT = "news"
        private const val CREATING_TRUE = "t"
        private const val DEFAULT_FNOP = "submit-page"
        private const val DEFAULT_SUBMIT_REDIRECT = "newest"
        private const val REGEX_INPUT = "<\\s*input[^>]*>"
        private const val REGEX_VALUE = "value[^\"]*\"([^\"]*)\""
        private const val REGEX_CREATE_ERROR_BODY = "<body>([^<]*)"
        private const val HEADER_LOCATION = "location"
        private const val HEADER_COOKIE = "cookie"
        private const val HEADER_SET_COOKIE = "set-cookie"
    }
}