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
import com.rnd.aedss_android.activity.RoomListActivity
import com.rnd.aedss_android.datamodel.ResponseData
import com.rnd.aedss_android.datamodel.body_model.PostNewPasswordBody
import com.rnd.aedss_android.utils.Constants.Companion.CHANGE_PASSWORD
import com.rnd.aedss_android.utils.Constants.Companion.EMPTY_INPUT
import com.rnd.aedss_android.utils.Constants.Companion.ERROR_NOTIFY
import com.rnd.aedss_android.utils.Constants.Companion.ERROR_NOT_EQUAL
import com.rnd.aedss_android.utils.Constants.Companion.PASSWORD_INTENT
import com.rnd.aedss_android.utils.Constants.Companion.RESET_PASSWORD
import com.rnd.aedss_android.utils.Constants.Companion.convertToMd5
import com.rnd.aedss_android.utils.api.RetrofitInstance
import com.rnd.aedss_android.utils.preferences.AuthenticationPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var title: TextView
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var submitBtn: CardView
    private lateinit var submitButton: Button
    private lateinit var loginBtn: TextView
    var rcvRoom: Int = 0
    val RESET_SUCCESS = "reset success"
    val CHANGE_SUCCESS = "change success"

    lateinit var session: AuthenticationPreferences
    var auth: String = ""
    var userid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        rcvRoom = intent.getIntExtra(PASSWORD_INTENT, 0)
        passwordInput = findViewById(R.id.password_input)
        confirmPasswordInput = findViewById(R.id.confirm_password_input)

        session = AuthenticationPreferences(this)
        auth = session.getAuthToken().toString()
        userid = session.getUserid().toString()

        title = findViewById(R.id.title_tv)
        if (rcvRoom == RESET_PASSWORD) {
            title.text = "Reset Password"
        } else if (rcvRoom == CHANGE_PASSWORD) {
            title.text = "Change Password"
        }

        submitBtn = findViewById(R.id.submit_btn)
        submitBtn.setOnClickListener {
            var password = passwordInput.text.toString()
            var confirmPassword = confirmPasswordInput.text.toString()

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                showAlertDialog(EMPTY_INPUT)
            } else {
                if (!password.equals(confirmPassword)) {
                    showAlertDialog(ERROR_NOT_EQUAL)
                } else {
                    changePasswordFunction(password)
                }
            }
        }
        submitButton = findViewById(R.id.submit_button)
        submitButton.setOnClickListener {
            var password = passwordInput.text.toString()
            var confirmPassword = confirmPasswordInput.text.toString()

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                showAlertDialog(EMPTY_INPUT)
            } else {
                if (!password.equals(confirmPassword)) {
                    showAlertDialog(ERROR_NOT_EQUAL)
                } else {
                    changePasswordFunction(password)
                }
            }
        }

        loginBtn = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            var intent: Intent = Intent(applicationContext, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun changePasswordFunction(password: String) {
        var postNewPasswordBody = PostNewPasswordBody(convertToMd5(password))
        RetrofitInstance.apiServiceInterface.changePassword(auth, userid, postNewPasswordBody)
            .enqueue(object : Callback<ResponseData>{
                override fun onResponse(
                    call: Call<ResponseData>,
                    response: Response<ResponseData>
                ) {
                    if (response?.body() == null) {
                        Log.d("Error change password: ", "body null")
                        showAlertDialog(ERROR_NOTIFY)
                        return
                    } else {
                        var result = response.body()
                        if (result != null) {
                            if (result.success == true) {
                                if (rcvRoom == RESET_PASSWORD) {
                                    showAlertDialog(RESET_SUCCESS)
                                    var headerList = response.headers()
                                    var newAuth: String = ""
                                    var newUserid: String = ""
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
                                    session.addAuthAndUser(newAuth, newUserid)
                                } else if (rcvRoom == CHANGE_PASSWORD) {
                                    showAlertDialog(CHANGE_SUCCESS)
                                }

                            } else {
                                showAlertDialog(ERROR_NOTIFY)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                    Log.d("Error change password: ", t.message.toString())
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
        var title = dialogView.findViewById<TextView>(R.id.title_dialog)

        if (command == EMPTY_INPUT) {
            alertText.text = "Please input all fields."
            okBtn.setOnClickListener {
                alertDialog.dismiss()
            }
            okSection.setOnClickListener {
                alertDialog.dismiss()
            }
        } else if (command == ERROR_NOTIFY) {
            alertText.text = "There is something wrong. Please try again."
            okBtn.setOnClickListener {
                alertDialog.dismiss()
                var intent: Intent = Intent(applicationContext, ChangePasswordActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            okSection.setOnClickListener {
                alertDialog.dismiss()
                var intent: Intent = Intent(applicationContext, ChangePasswordActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        } else if (command == ERROR_NOT_EQUAL) {
            alertText.text = "The password confirmation does not match."
            okBtn.setOnClickListener {
                alertDialog.dismiss()
            }
            okSection.setOnClickListener {
                alertDialog.dismiss()
            }
        } else if (command == RESET_SUCCESS) {
            title.text = getString(R.string.NOTI_TEXT)
            alertText.text = "Reset password successfully. Click OK to return to login page."
            okBtn.setOnClickListener {
                alertDialog.dismiss()
                var intent: Intent = Intent(applicationContext, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            okSection.setOnClickListener {
                alertDialog.dismiss()
                var intent: Intent = Intent(applicationContext, LoginActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        } else if (command == CHANGE_SUCCESS) {
            title.text = getString(R.string.NOTI_TEXT)
            alertText.text = "Change password successfully. Click OK to continue."
            okBtn.setOnClickListener {
                alertDialog.dismiss()
                var intent: Intent = Intent(applicationContext, RoomListActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            okSection.setOnClickListener {
                alertDialog.dismiss()
                var intent: Intent = Intent(applicationContext, RoomListActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }
}
