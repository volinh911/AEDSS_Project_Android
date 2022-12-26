package com.rnd.aedss_android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.rnd.aedss_android.R

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            val intent = Intent(this, RoomListActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
    }
}