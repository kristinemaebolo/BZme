package com.example.bzme.Model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.bzme.R
import androidx.annotation.RequiresApi
import android.app.Notification
class Notification(p0: Context): Notification(){
    private val context = p0
    private val CHANNEL = "bz_me"

    /**
     * from Android docs
     * Because you must create the notification channel before posting any notifications on Android 8.0 and higher,
     * you should execute this code as soon as your app starts.
     * It's safe to call this repeatedly because creating an existing notification channel performs no operation.
     *
     * So mao to ato gi tawag sa MainActivity ni nga function (line 49-51)
     * ambot ngano need ni nga function wa man gi explain ngano,
     * details man guro ni sa channel then mao ni container sa notification nga ma create
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var name = "BZme NOTIFICATIONS"
            var descriptionText = "This is a description"
            var importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL,name,importance).apply {
                description = descriptionText
            }

            val notificationManager : NotificationManager = context?.getSystemService(
                NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * notificationId kay unique dapat aron di ma overwrite ang existing notification
     */
    fun sendNotiication(title: String, content: String, notificationId: Int){
        var builder = NotificationCompat.Builder(context,CHANNEL)
            .setSmallIcon(R.mipmap.logob_round)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    }
}