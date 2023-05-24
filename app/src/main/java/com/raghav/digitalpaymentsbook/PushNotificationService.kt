package com.raghav.digitalpaymentsbook

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.raghav.digitalpaymentsbook.ui.activity.MainActivity

class PushNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification!!.title
        val text = message.notification!!.body
        var link: String? = null
        if (message.data.size > 0 && message.data.containsKey("link")) {
            link = message.data["link"]
        }
        createNotification(title, text, link)
        super.onMessageReceived(message)
    }

    fun createNotification(title: String?, text: String?, link: String?) {

        // Create an Intent for the activity you want to start
//        Intent resultIntent = new Intent(this, MainActivity.class);
        val resultIntent: Intent
        if (link != null) {
            resultIntent = Intent(Intent.ACTION_VIEW)
            resultIntent.data = Uri.parse(link)
        } else {
            resultIntent = Intent(this, MainActivity::class.java)
        }
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val CHANNEL_ID = "HEADS_UP_NOTIFICATION"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Heads Up Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
            val notification = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
            NotificationManagerCompat.from(this).notify(1, notification.build())
        } else {
            val notificationPopup = Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .build()
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notificationPopup)
        }
    }
}