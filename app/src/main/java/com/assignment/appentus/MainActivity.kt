package com.assignment.appentus

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial


private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (application as GlobalApp).preferenceRepository
            .nightModeLive.observe(this) { nightMode ->
                nightMode?.let { delegate.localNightMode = it }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
         val inflater:MenuInflater=menuInflater
        inflater.inflate(R.menu.switch_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val inflatedMenu=menu.findItem(R.id.app_bar_switch)
        val view=inflatedMenu.actionView
                val darkThemeSwitch: SwitchMaterial = view.findViewById(R.id.dark_light_mode)
        val preferenceRepository = (this.application as GlobalApp).preferenceRepository

        preferenceRepository.isDarkThemeLive.observe(this) { isDarkTheme ->
            isDarkTheme?.let { darkThemeSwitch.isChecked = it }
        }

        darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            preferenceRepository.isDarkTheme = checked
        }
        
        return super.onPrepareOptionsMenu(menu)
    }




}