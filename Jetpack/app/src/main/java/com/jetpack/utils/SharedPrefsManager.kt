package com.jetpack.utils

import android.content.Context
import android.content.SharedPreferences

object SharedPrefsManager {

    private const val PREF_NAME = "manga"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setIsLogin(context: Context, status: Boolean) {
        getPrefs(context).edit().putBoolean(Constants.IS_LOGIN, status).apply()
    }

    fun getIsLogin(context: Context): Boolean {
        return getPrefs(context).getBoolean(Constants.IS_LOGIN, false)
    }
}