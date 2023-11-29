package com.dicoding.habitapp.setting

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.dicoding.habitapp.R
import com.dicoding.habitapp.utils.DarkMode

class SettingsActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Notifications permission granted")
            } else {
                showToast("Notifications will not show without permission")
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT > 32) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            //TODO 11 : Update theme based on value in ListPreference Done✅
            setupDarkModePreference()
        }

        private fun setupDarkModePreference() {
            val switchDarkMode: ListPreference? = findPreference(getString(R.string.pref_key_dark))
            switchDarkMode?.setOnPreferenceChangeListener { _, newValue ->
                handleDarkModeChange(newValue.toString())
            }
        }

        private fun handleDarkModeChange(value: String): Boolean {
            val mode = when (value) {
                getString(R.string.pref_dark_follow_system) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        DarkMode.FOLLOW_SYSTEM.value
                    } else {
                        DarkMode.ON.value
                    }
                }
                getString(R.string.pref_dark_off) -> DarkMode.OFF.value
                else -> DarkMode.ON.value
            }

            updateTheme(mode)
            return true
        }

        private fun updateTheme(mode: Int) {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
        }
    }
}