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

import kotlin.Throws
import android.accounts.NetworkErrorException
import android.accounts.AccountAuthenticatorResponse
import android.os.Bundle
import android.content.Intent
import io.github.hidroh.materialistic.LoginActivity
import android.accounts.AccountManager
import android.content.Context

class AccountAuthenticator(private val mContext: Context) : EmptyAccountAuthenticator(
    mContext
) {
    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String,
        requiredFeatures: Array<String>,
        options: Bundle
    ): Bundle {
        val intent = Intent(mContext, LoginActivity::class.java)
        intent.putExtra(LoginActivity.EXTRA_ADD_ACCOUNT, true)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }
}