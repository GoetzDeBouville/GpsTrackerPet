package com.hellcorp.gpstrackerpet.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.hellcorp.gpstrackerpet.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
    }
}