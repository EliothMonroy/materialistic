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

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import io.github.hidroh.materialistic.accounts.UserServices
import java.lang.ref.WeakReference
import javax.inject.Inject

open class LoginActivity : AccountAuthenticatorActivity() {
    @JvmField
    @Inject
    var mUserServices: UserServices? = null

    @JvmField
    @Inject
    var mAccountManager: AccountManager? = null
    private var mLoginButton: View? = null
    private var mRegisterButton: View? = null
    private var mUsernameLayout: TextInputLayout? = null
    private var mPasswordLayout: TextInputLayout? = null
    private var mUsernameEditText: EditText? = null
    private var mPasswordEditText: EditText? = null
    private var mUsername: String? = null
    private var mPassword: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username = Preferences.getUsername(this)
        val addAccount = intent.getBooleanExtra(EXTRA_ADD_ACCOUNT, false)
        setContentView(R.layout.activity_login)
        mUsernameLayout = findViewById(R.id.textinput_username)
        mPasswordLayout = findViewById(R.id.textinput_password)
        mUsernameEditText = findViewById(R.id.edittext_username)
        mLoginButton = findViewById(R.id.login_button)
        mRegisterButton = findViewById(R.id.register_button)
        if (!addAccount && !TextUtils.isEmpty(username)) {
            setTitle(R.string.re_enter_password)
            mUsernameEditText?.setText(username)
            mRegisterButton?.visibility = View.GONE
        }
        mPasswordEditText = findViewById(R.id.edittext_password)
        mLoginButton?.setOnClickListener {
            if (!validate()) {
                return@setOnClickListener
            }
            mLoginButton?.isEnabled = false
            mRegisterButton?.isEnabled = false
            login(
                mUsernameEditText?.text.toString(),
                mPasswordEditText?.text.toString(),
                false
            )
        }
        mRegisterButton?.setOnClickListener {
            if (!validate()) {
                return@setOnClickListener
            }
            mLoginButton?.isEnabled = false
            mRegisterButton?.isEnabled = false
            login(
                mUsernameEditText?.text.toString().trim { it <= ' ' },
                mPasswordEditText?.text.toString().trim { it <= ' ' },
                true
            )
        }
    }

    override val isDialogTheme: Boolean
        get() = true

    private fun validate(): Boolean {
        mUsernameLayout!!.isErrorEnabled = false
        mPasswordLayout!!.isErrorEnabled = false
        if (mUsernameEditText!!.length() == 0) {
            mUsernameLayout!!.error = getString(R.string.username_required)
        }
        if (mPasswordEditText!!.length() == 0) {
            mPasswordLayout!!.error = getString(R.string.password_required)
        }
        return mUsernameEditText!!.length() > 0 && mPasswordEditText!!.length() > 0
    }

    private fun login(username: String, password: String, createAccount: Boolean) {
        mUsername = username
        mPassword = password
        mUserServices!!.login(username, password, createAccount, LoginCallback(this))
    }

    fun onLoggedIn(successful: Boolean, errorMessage: String?) {
        mLoginButton!!.isEnabled = true
        mRegisterButton!!.isEnabled = true
        if (successful) {
            addAccount(mUsername, mPassword)
            Toast.makeText(this, getString(R.string.welcome, mUsername), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                this,
                if (TextUtils.isEmpty(errorMessage)) getString(R.string.login_failed) else errorMessage,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addAccount(username: String?, password: String?) {
        val account = Account(username, BuildConfig.APPLICATION_ID)
        mAccountManager!!.addAccountExplicitly(account, password, null)
        mAccountManager!!.setPassword(account, password) // for re-login with updated password
        val bundle = Bundle()
        bundle.putString(AccountManager.KEY_ACCOUNT_NAME, username)
        bundle.putString(AccountManager.KEY_ACCOUNT_TYPE, BuildConfig.APPLICATION_ID)
        setAccountAuthenticatorResult(bundle)
        Preferences.setUsername(this, username)
        finish()
    }

    internal class LoginCallback(loginActivity: LoginActivity?) : UserServices.Callback() {
        private val mLoginActivity: WeakReference<LoginActivity?>

        init {
            mLoginActivity = WeakReference(loginActivity)
        }

        override fun onDone(successful: Boolean) {
            if (mLoginActivity.get() != null && !mLoginActivity.get()!!.isActivityDestroyed) {
                mLoginActivity.get()!!.onLoggedIn(successful, null)
            }
        }

        override fun onError(throwable: Throwable) {
            if (mLoginActivity.get() != null && !mLoginActivity.get()!!.isActivityDestroyed) {
                mLoginActivity.get()!!
                    .onLoggedIn(false, throwable.message)
            }
        }
    }

    companion object {
        @JvmField
        val EXTRA_ADD_ACCOUNT = LoginActivity::class.java.name + ".EXTRA_ADD_ACCOUNT"
    }
}