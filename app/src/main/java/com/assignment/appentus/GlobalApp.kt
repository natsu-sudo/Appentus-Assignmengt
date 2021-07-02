package com.assignment.appentus

import android.app.Application
import android.content.Context
import com.assignment.appentus.ui.PreferenceRepository

class GlobalApp:Application() {
    lateinit var preferenceRepository: PreferenceRepository

    override fun onCreate() {
        super.onCreate()
        preferenceRepository = PreferenceRepository(
            getSharedPreferences(DEFAULT_PREFERENCES, Context.MODE_PRIVATE)
        )

    }

    companion object {
        const val DEFAULT_PREFERENCES = "default_preferences"
    }
}