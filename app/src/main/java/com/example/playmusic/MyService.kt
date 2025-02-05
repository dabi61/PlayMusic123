package com.example.playmusic

import android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playmusic.Constance.CHANNEL_ID
import com.example.playmusic.Constance.PUT_EXTRA_NOTIFICATION
import com.example.playmusic.Constance.PUT_EXTRA_SONG
import java.util.Date

class MyService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate() {
        super.onCreate()
        Log.e("Service", "onCreate")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val song = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(PUT_EXTRA_SONG, Song::class.java)
        } else {
            intent.getParcelableExtra(PUT_EXTRA_SONG)
        }

        if (song != null) {
            startMusic(song)
            sendNotification(song)
        }

        return START_NOT_STICKY
    }

    private fun startMusic(song: Song) {
        mediaPlayer = MediaPlayer.create(applicationContext, song.resource)
        mediaPlayer.start()
    }

    private fun sendNotification(song: Song) {

        val intent = Intent(this, MainActivity::class.java)
        val penddingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val bitmap = BitmapFactory.decodeResource(resources, song.image)

        val remoteViews = RemoteViews(packageName, R.layout.custom_notification_music)
        remoteViews.setTextViewText(R.id.tv_title_song, song.title)
        remoteViews.setTextViewText(R.id.tv_single_song, song.single)
        remoteViews.setImageViewBitmap(R.id.iv_song, bitmap)
        remoteViews.setImageViewResource(R.id.iv_play_or_pause, R.drawable.ic_pause)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(penddingIntent)
            .setCustomContentView(remoteViews)
            .setSound(null)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
            ServiceCompat.startForeground(this, getNotificationId(), notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("Service", "onDestroy")
            mediaPlayer.stop()
            mediaPlayer.release()
    }

    private fun getNotificationId() : Int {
        return Date().time.toInt()
    }
}