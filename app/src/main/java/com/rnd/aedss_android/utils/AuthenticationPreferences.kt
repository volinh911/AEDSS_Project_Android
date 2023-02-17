package com.rnd.aedss_android.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.rnd.aedss_android.activity.LoginActivity
import com.rnd.aedss_android.utils.Constants.Companion.IS_LOGGED_IN
import com.rnd.aedss_android.utils.Constants.Companion.PASSWORD
import com.rnd.aedss_android.utils.Constants.Companion.PREF_NAME
import com.rnd.aedss_android.utils.Constants.Companion.USERNAME

class AuthenticationPreferences {
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var context: Context
    var PRIVATE_MODE : Int = 0

    constructor(context: Context) {
        this.context = context
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun createLoginSession(username: String, password: String) {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putString(USERNAME, username)
        editor.putString(PASSWORD, password)
        editor.commit()
    }

    fun checkLogin() {
        if (!this.isLoggedIn()) {
            var intent: Intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun getUserDetails(): HashMap<String, String> {
        var user: Map<String, String> = HashMap<String, String>()
        (user as HashMap).put(USERNAME, pref.getString(USERNAME, null)!!)
        (user as HashMap).put(PASSWORD, pref.getString(PASSWORD, null)!!)
        return user
    }

    fun logoutUser() {
        editor.clear()
        editor.commit()
        var intent: Intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGGED_IN, false)
    }
}