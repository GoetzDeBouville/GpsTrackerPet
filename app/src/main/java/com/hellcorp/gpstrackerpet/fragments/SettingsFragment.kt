package com.hellcorp.gpstrackerpet.fragments

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import com.hellcorp.gpstrackerpet.R

class SettingsFragment : PreferenceFragmentCompat() {
    private var timePref: Preference? = null
    private var rootView: View? = null
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
        init()
        initPrefs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timePref = null
        rootView = null
    }

    private fun init() {
        timePref = findPreference("update_time_key")
        val changeSiltener = onChangeListener()
        timePref!!.onPreferenceChangeListener = changeSiltener
    }

    private fun onChangeListener(): OnPreferenceChangeListener {
        return OnPreferenceChangeListener { preference, newValue ->
            val nameArray = resources.getStringArray(R.array.loc_time_update_name)
            val valueArray = resources.getStringArray(R.array.loc_time_update_value)
            val title  = preference.title.toString().substringBefore(":")
            preference.title = "$title: ${nameArray[valueArray.indexOf(newValue)]}"
            true
        }
    }

    private fun initPrefs(){
        val preference = timePref!!.preferenceManager.sharedPreferences
        val nameArray = resources.getStringArray(R.array.loc_time_update_name)
        val valueArray = resources.getStringArray(R.array.loc_time_update_value)
        val title  = timePref!!.title
        timePref!!.title = "$title: ${nameArray[valueArray.indexOf(preference?.getString("update_time_key", "3000"))]}"
    }
}
