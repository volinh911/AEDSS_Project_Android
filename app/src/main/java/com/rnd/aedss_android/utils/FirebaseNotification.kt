package com.rnd.aedss_android.utils

import android.app.Notification
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
import com.rnd.aedss_android.utils.Constants.Companion.convertToMd5


class FirebaseNotification: FirebaseMessagingService() {

    fun generateNotification(title: String, body: String) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val customNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.noti_icon)
//            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(body)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)

        notificationManager.notify(0, customNotification)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("noti", "Message Notification Body: ${it.title}")
            Log.d("noti", "Message Notification Body: ${it.body}")
            generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }

    }
}