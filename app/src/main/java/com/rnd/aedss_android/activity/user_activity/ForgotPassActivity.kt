package com.rnd.aedss_android.activity.user_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.rnd.aedss_android.R
import com.rnd.aedss_android.datamodel.ResponseData
import com.rnd.aedss_android.datamodel.body_model.PostValidateUserBody
import com.rnd.aedss_android.utils.Constants
import com.rnd.aedss_android.utils.Constants.Companion.EMPTY_INPUT
import com.rnd.aedss_android.utils.Constants.Companion.ERROR_NOTIFY
import com.rnd.aedss_android.utils.Constants.Companion.PASSWORD_INTENT
import com.rnd.aedss_android.utils.Constants.Companion.RESET_PASSWORD
import com.rnd.aedss_android.utils.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPassActivity : AppCompatActivity() {

    private lateinit var submitEmailBtn: Button
    private lateinit var submitTokenBtn: Button
    private lateinit var emailBtn: CardView
    private lateinit var tokenBtn: CardView
    private lateinit var emailInput: EditText
    private lateinit var tokenInput: EditText
    private lateinit var tokenText: TextView
    private lateinit var loginBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        loginBtn = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            var intent: Intent = Intent(applicationContext, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        emailBtn = findViewById(R.id.email_button)
        submitEmailBtn = findViewById(R.id.submit_email_btn)
        emailInput = findViewById(R.id.email_input)
        submitEmailBtn.setOnClickListener {
            var email = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                showAlertDialog(EMPTY_INPUT)
            } else {
                checkEmail(email)
            }
        }
        emailBtn.setOnClickListener {
            var email = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                showAlertDialog(EMPTY_INPUT)
            } else {
                checkEmail(email)
            }
        }

        tokenInput = findViewById(R.id.token_input)
        tokenText = findViewById(R.id.token_tv)
        submitTokenBtn = findViewById(R.id.submit_token_btn)
        tokenBtn = findViewById(R.id.token_button)
    }

    private fun checkEmail(email: String) {
        RetrofitInstance.apiServiceInterface.checkEmail(email)
            .enqueue(object: Callback<ResponseData>{
                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    if (response?.body() == null) {
                        Log.d("Error check email: ", "body null")
                        showAlertDialog(ERROR_NOTIFY)
                    } else {
                        var result = response.body()
                        if (result != null) {
                            if (result.success == true) {
                                sendToken(email)
                            } else {
                                showAlertDialog(ERROR_NOTIFY)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    Log.d("Error check email: ", t.message.toString())
                    showAlertDialog(ERROR_NOTIFY)
                    return
                }

            })
    }

    private fun sendToken(email: String) {
        RetrofitInstance.apiServiceInterface.sendToken(email)
            .enqueue(object: Callback<ResponseData>{
                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    if (response?.body() == null) {
                        Log.d("Error send token: ", "body null")
                        showAlertDialog(ERROR_NOTIFY)
                        return
                    } else {
                        var result = response.body()
                        if (result != null) {
                            if (result.success == true) {
                                emailBtn.visibility = View.GONE
                                submitEmailBtn.visibility = View.GONE

                                tokenInput.visibility = View.VISIBLE
                                tokenText.visibility = View.VISIBLE
                                submitTokenBtn.visibility = View.VISIBLE
                                tokenBtn.visibility = View.VISIBLE

                                submitTokenBtn.setOnClickListener {
                                    var token = tokenInput.text.toString().trim()
                                    validateToken(email, token)
                                }

                                tokenBtn.setOnClickListener {
                                    var token = tokenInput.text.toString().trim()
                                    validateToken(email, token)
                                }
                            } else {
                                showAlertDialog(ERROR_NOTIFY)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    Log.d("Error send token: ", t.message.toString())
                    showAlertDialog(ERROR_NOTIFY)
                    return
                }

            })
    }

    private fun validateToken(email: String, token: String) {
        var body = PostValidateUserBody(email, token)
        RetrofitInstance.apiServiceInterface.validateToken(body)
            .enqueue(object : Callback<ResponseData> {
                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    if (response?.body() == null) {
                        Log.d("Error validate token: ", "body null")
                        showAlertDialog(ERROR_NOTIFY)
                        return
                    } else {
                        var result = response.body()
                        if (result != null) {
                            if (result.success == true) {
                                var intent: Intent = Intent(applicationContext, ChangePasswordActivity::class.java)
                                intent.putExtra(PASSWORD_INTENT, RESET_PASSWORD)
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            } else {
                                showAlertDialog(ERROR_NOTIFY)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    Log.d("Error validate token: ", t.message.toString())
                    showAlertDialog(ERROR_NOTIFY)
                    return
                }

            })
    }

    private fun showAlertDialog(command: String) {
        val dialogView = View.inflate(this, R.layout.alert_dialog, null)
        val builder = android.app.AlertDialog.Builder(this)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        var okBtn = dialogView.findViewById<Button>(R.id.ok_btn)
        var okSection = dialogView.findViewById<CardView>(R.id.ok_section)
        var alertText = dialogView.findViewById<TextView>(R.id.alert_dialog_text)

        okBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        okSection.setOnClickListener {
            alertDialog.dismiss()
        }

        if (command == EMPTY_INPUT) {
            alertText.text = "Please input all fields."
        } else if (command == ERROR_NOTIFY) {
            alertText.text = "There is something wrong. Please check again."
        }
    }
}