package com.SafeWord.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHelper {

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getString(context: Context, key: String, defaultValue: String = ""): String {
        return getPrefs(context).getString(key, defaultValue) ?: defaultValue
    }

    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int {
        return getPrefs(context).getInt(key, defaultValue)
    }

    fun putString(context: Context, key: String, value: String) {
        getPrefs(context).edit().putString(key, value).apply()
    }

    fun putInt(context: Context, key: String, value: Int) {
        getPrefs(context).edit().putInt(key, value).apply()
    }
}
