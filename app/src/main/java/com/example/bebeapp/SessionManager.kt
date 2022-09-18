package com.example.bebeapp

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    var sharedPreference: SharedPreferences
    var editor: SharedPreferences.Editor

    init {
        sharedPreference = context.getSharedPreferences("AppKey", 0)
        editor = sharedPreference.edit()
        editor.apply()
    }

    fun setFlag(flag: Boolean) {
        editor.putBoolean("KEY_FLAG", flag)
        editor.commit()
    }

    fun getFlag(): Boolean {
        return sharedPreference.getBoolean("KEY_FLAG", false)
    }

    fun setCurrentTime(currentTime: String) {
        editor.putString("KEY_TIME", currentTime)
        editor.commit()
    }

    fun getCurrentTime(): String {
        return sharedPreference.getString("KEY_TIME", "").toString()
    }

}

