package com.example.playmusic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.playmusic.Constance.CHANNEL_ID

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createChannelNotification()
    }

    private fun createChannelNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "CHANNEL SERVICE EXAMPLE",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            channel.setSound(null, null)
//            val notificationManager = getSystemService(NotificationManager::class.java)
//            notificationManager.createNotificationChannel(channel)
//        }


    }
}