package com.assignment.appentus.util

import android.content.Context
import android.content.SharedPreferences

class UserSharedPreferences {
    companion object{
        fun initializeSharedPreferencesForSavedPage(context: Context): SharedPreferences {
            return context.getSharedPreferences(Constants.SAVED_PAGE, Context.MODE_PRIVATE)
        }

    }
}