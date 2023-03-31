package com.rnd.aedss_android.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rnd.aedss_android.R
import com.rnd.aedss_android.activity.user_activity.LoginActivity
import com.rnd.aedss_android.utils.Constants.Companion.CHANNEL_ID
import com.rnd.aedss_android.utils.Constants.Companion.CHANNEL_NAME


class FirebaseNotification: FirebaseMessagingService() {

    fun getRemoteView(title: String, body: String): RemoteViews {
        val remoteView = RemoteViews(CHANNEL_NAME, R.layout.notification)
        remoteView.setTextViewText(R.id.noti_title, title)
        remoteView.setTextViewText(R.id.noti_body, body)
        remoteView.setImageViewResource(R.id.noti_logo, R.drawable.noti_icon)

        return remoteView
    }

    fun generateNotification(title: String, body: String) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.noti_icon)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, body))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("noti", "From: ${remoteMessage.from}")
        Log.d("noti", "Content: ${remoteMessage.notification!!.body!!}")

        if (remoteMessage.notification != null) {
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }
    }

    // [START on_new_token]
    /**
     * Called if the FCM registration token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the
     * FCM registration token is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d("onNewToken", "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d("sendToken", "sendRegistrationTokenToServer($token)")
    }
}