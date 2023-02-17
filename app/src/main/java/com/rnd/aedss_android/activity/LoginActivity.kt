package com.rnd.aedss_android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.cardview.widget.CardView
import com.rnd.aedss_android.R
import com.rnd.aedss_android.utils.AuthenticationPreferences
import com.rnd.aedss_android.utils.Constants.Companion.PASSWORD_KEY
import com.rnd.aedss_android.utils.Constants.Companion.USERNAME_KEY
import java.math.BigInteger
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBtn: Button
    private lateinit var userNameInput: EditText
    private lateinit var passwordInput: EditText

    lateinit var session: AuthenticationPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = AuthenticationPreferences(this)

        loginBtn = findViewById(R.id.login_btn)
        userNameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)

        if(session.isLoggedIn()) {
            var intent: Intent = Intent(applicationContext, RoomListActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }



        loginBtn.setOnClickListener {

            var password = passwordInput.text.toString().trim()
            var username = userNameInput.text.toString().trim()

            var hashPass = convertToMd5(password)
            var hashUser = convertToMd5(username)

            if (username.isEmpty() && password.isEmpty()) {
                showLoginAlertDialog()
            } else {
                if (convertToMd5(username) == USERNAME_KEY && convertToMd5(password) == PASSWORD_KEY) {
                    session.createLoginSession(username, password)
                    var intent: Intent = Intent(applicationContext, RoomListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    showLoginAlertDialog()
                }
            }
        }
    }

    fun convertToMd5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    private fun showLoginAlertDialog() {
        val dialogView = View.inflate(this, R.layout.login_alert_dialog, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var okBtn = dialogView.findViewById<Button>(R.id.ok_btn)
        var okSection = dialogView.findViewById<CardView>(R.id.ok_section)

        okBtn.setOnClickListener{
            alertDialog.dismiss()
        }
        okSection.setOnClickListener{
            alertDialog.dismiss()
        }

    }
}