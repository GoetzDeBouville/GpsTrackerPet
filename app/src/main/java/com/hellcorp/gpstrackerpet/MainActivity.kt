package com.hellcorp.gpstrackerpet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hellcorp.gpstrackerpet.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        provideClickListeners()
    }

    private fun provideClickListeners() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.item_home -> Log.e("MainActivity", "item_home")
                R.id.item_tracks -> Log.e("MainActivity", "item_tracks")
                R.id.item_settings -> Log.e("MainActivity", "item_settings")
            }
            true
        }
    }
}