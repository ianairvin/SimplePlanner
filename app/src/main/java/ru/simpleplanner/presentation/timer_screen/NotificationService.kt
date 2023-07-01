package ru.simpleplanner.presentation.timer_screen

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.NotificationCompat
import ru.simpleplanner.presentation.MainActivity


class NotificationService(private val context: Context) {
    private val notificationChannel = NotificationChannel(
        CHANNEL_ID,
        "Notification when timer finish", NotificationManager.IMPORTANCE_HIGH
    )
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init{
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        notificationManager.createNotificationChannel(notificationChannel)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun show(isWorkScreen: Boolean){
        val intent = Intent(context, MainActivity::class.java)
            intent.flags  = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Simple Planner")
            .setContentText(when(isWorkScreen) {
                true -> "Рабочее время закончилось. Пора отдыхать!"
                false -> "Время отдыха закончилось. Пора работать!"
            })
            .setSmallIcon(ru.simpleplanner.R.drawable.date_range)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }

    companion object {
        const val CHANNEL_ID = "finish_timer"
    }
}