package com.tjackapps.besttodolist.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tjackapps.besttodolist.R
import com.tjackapps.besttodolist.databinding.MainActivityBinding
import com.tjackapps.besttodolist.ui.group.GroupFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, GroupFragment.newInstance())
                    .commitNow()
        }
    }
}