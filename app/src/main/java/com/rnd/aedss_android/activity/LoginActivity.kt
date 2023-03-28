package com.rnd.aedss_android.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.rnd.aedss_android.R
import com.rnd.aedss_android.datamodel.ResponseData
import com.rnd.aedss_android.utils.preferences.AuthenticationPreferences
import com.rnd.aedss_android.utils.Constants.Companion.convertToMd5
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.viewmodel.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBtn: Button
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    lateinit var session: AuthenticationPreferences

    // Declare the launcher at the top of your Activity/Fragment:
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted: Boolean ->
//        if (isGranted) {
//            // FCM SDK (and your app) can post notifications.
//        } else {
//            // TODO: Inform user that that your app will not show notifications.
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        requestPermissionLauncher
//        logRegToken()

        session = AuthenticationPreferences(this)

        loginBtn = findViewById(R.id.submit_post_btn)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)

        if (session.isLoggedIn()) {
            var intent: Intent = Intent(applicationContext, RoomListActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        loginBtn.setOnClickListener {

            var password = passwordInput.text.toString().trim()
            var email = emailInput.text.toString().trim()

            if (email.isEmpty() && password.isEmpty()) {
                showLoginAlertDialog()
            } else {
                postLoginUser(email, convertToMd5(password))
            }
        }
    }

    private fun showLoginAlertDialog() {
        val dialogView = View.inflate(this, R.layout.alert_dialog, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var okBtn = dialogView.findViewById<Button>(R.id.ok_btn)
        var okSection = dialogView.findViewById<CardView>(R.id.ok_section)

        okBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        okSection.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun postLoginUser(email: String, password: String) {
        var user = User(email, password)
        RetrofitInstance.apiServiceInterface.postLogin(user)
            .enqueue(object : Callback<ResponseData> {
                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    if (response?.body() == null) {
                        showLoginAlertDialog()
                        return
                    }

                    var headerList = response.headers()
                    var auth: String = ""
                    var userid: String = ""
                    for (header in headerList) {
                        if (header.first == "auth") {
                            auth = header.second
                            continue
                        }
                        if (header.first == "userid") {
                            userid = header.second
                            continue
                        }
                    }

                    session.createLoginSession(email, password, auth, userid)
                    var intent: Intent = Intent(applicationContext, RoomListActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    Log.d("Error login: ", t.message.toString())
                }

            })
    }

//    fun logRegToken() {
//        Firebase.messaging.getToken().addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@addOnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            // Log and toast
//            val msg = "FCM Registration token: $token"
//            Log.d(TAG, msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
//        }
//    }

}