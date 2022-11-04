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

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat

class PreferencesActivity : ThemedActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        setTitle(intent.getIntExtra(EXTRA_TITLE, 0))
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_HOME or
                ActionBar.DISPLAY_HOME_AS_UP or ActionBar.DISPLAY_SHOW_TITLE
        if (savedInstanceState == null) {
            val args = Bundle()
            args.putInt(EXTRA_PREFERENCES, intent.getIntExtra(EXTRA_PREFERENCES, 0))
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.content_frame,
                    Fragment.instantiate(this, SettingsFragment::class.java.name, args),
                    SettingsFragment::class.java.name
                )
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            Preferences.sync(preferenceManager)
        }

        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            if (arguments != null) {
                addPreferencesFromResource(requireArguments().getInt(EXTRA_PREFERENCES))
            }
        }
    }

    companion object {
        @JvmField
        val EXTRA_TITLE = PreferencesActivity::class.java.name + ".EXTRA_TITLE"

        @JvmField
        val EXTRA_PREFERENCES = PreferencesActivity::class.java.name + ".EXTRA_PREFERENCES"
    }
}