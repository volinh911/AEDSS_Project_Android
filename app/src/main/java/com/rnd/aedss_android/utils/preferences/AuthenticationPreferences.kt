package com.rnd.aedss_android.utils.preferences

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.rnd.aedss_android.activity.user_activity.LoginActivity
import com.rnd.aedss_android.utils.Constants.Companion.AUTHENTICATION_TOKEN
import com.rnd.aedss_android.utils.Constants.Companion.IS_LOGGED_IN
import com.rnd.aedss_android.utils.Constants.Companion.PASSWORD
import com.rnd.aedss_android.utils.Constants.Companion.AUTH_PREF
import com.rnd.aedss_android.utils.Constants.Companion.EMAIL
import com.rnd.aedss_android.utils.Constants.Companion.USER_ID
import com.rnd.aedss_android.viewmodel.User

class AuthenticationPreferences {
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var context: Context
    var PRIVATE_MODE: Int = 0

    constructor(context: Context) {
        this.context = context
        pref = context.getSharedPreferences(AUTH_PREF, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun createLoginSession(email: String, password: String, auth: String, userid: String) {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putString(EMAIL, email)
        editor.putString(PASSWORD, password)
        editor.putString(AUTHENTICATION_TOKEN, auth)
        editor.putString(USER_ID, userid)
        editor.commit()
    }

    fun addAuthAndUser(auth: String, userid: String) {
        editor.putBoolean(IS_LOGGED_IN, true)
        editor.putString(AUTHENTICATION_TOKEN, auth)
        editor.putString(USER_ID, userid)
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

    fun getUserDetails(): User {
        var user = User(pref.getString(EMAIL, null)!!, pref.getString(PASSWORD, null)!!)
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

    fun getAuthToken(): String? {
        return pref.getString(AUTHENTICATION_TOKEN, "")
    }

    fun getUserid(): String? {
        return pref.getString(USER_ID, "")
    }
}