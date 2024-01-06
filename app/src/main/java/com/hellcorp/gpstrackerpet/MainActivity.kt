package com.hellcorp.gpstrackerpet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hellcorp.gpstrackerpet.databinding.ActivityMainBinding
import com.hellcorp.gpstrackerpet.fragments.MainFragment
import com.hellcorp.gpstrackerpet.fragments.SettingsFragment
import com.hellcorp.gpstrackerpet.fragments.ViewTrackFragment
import com.hellcorp.gpstrackerpet.utils.openFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        provideClickListeners()
        openFragment(MainFragment.newInstance())
    }

    private fun provideClickListeners() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> openFragment(MainFragment.newInstance())
                R.id.item_tracks -> openFragment(ViewTrackFragment.newInstance())
                R.id.item_settings -> openFragment(SettingsFragment())
            }
            true
        }
    }
}