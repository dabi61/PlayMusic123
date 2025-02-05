package com.example.playmusic

import android.Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.session.PlaybackState.ACTION_PAUSE
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.playmusic.Constance.ACTION_CLEAR
import com.example.playmusic.Constance.ACTION_PAUSE
import com.example.playmusic.Constance.ACTION_RESUME
import com.example.playmusic.Constance.CHANNEL_ID
import com.example.playmusic.Constance.PUT_EXTRA_SONG
import java.util.Date

class MyService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var notificationManager : NotificationManager
    private var isPlaying : Boolean = false
    private lateinit var mSong : Song
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
            mSong = song
            startMusic(song)
            sendNotification(song)
        }
        val actionMusic = intent.getIntExtra(Constance.PUT_EXTRA_ACTION_MUSIC_SERVICE, 0)
        handActionMusic(actionMusic)
        return START_NOT_STICKY
    }

    private fun startMusic(song: Song) {
        mediaPlayer = MediaPlayer.create(applicationContext, song.resource)
        isPlaying = true
        mediaPlayer.start()
        sendActionToActivity(Constance.ACTION_START)
    }

    private fun sendNotification(song: Song) {

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val bitmap = BitmapFactory.decodeResource(resources, song.image)

        val remoteViews = RemoteViews(packageName, R.layout.custom_notification_music)
        remoteViews.setTextViewText(R.id.tv_title_song, song.title)
        remoteViews.setTextViewText(R.id.tv_single_song, song.single)
        remoteViews.setImageViewBitmap(R.id.iv_song, bitmap)
        remoteViews.setImageViewResource(R.id.iv_play_or_pause, R.drawable.ic_pause)


        if(isPlaying) {
            remoteViews.setOnClickPendingIntent(R.id.iv_play_or_pause, getPendingIntent(this, Constance.ACTION_PAUSE))
            remoteViews.setImageViewResource(R.id.iv_play_or_pause, R.drawable.ic_pause)
        } else {
            remoteViews.setOnClickPendingIntent(R.id.iv_play_or_pause, getPendingIntent(this, ACTION_RESUME))
            remoteViews.setImageViewResource(R.id.iv_play_or_pause, R.drawable.ic_start)
        }

        remoteViews.setOnClickPendingIntent(R.id.iv_close, getPendingIntent(this, ACTION_CLEAR))


        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                CHANNEL_ID,
                "Hi",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(chan)

            Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_pause) // the status icon
                .setWhen(System.currentTimeMillis()) // the time stamp
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent)
                .build()
        } else {
            Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_pause)
                .setWhen(System.currentTimeMillis()) // the time stamp
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent)
                .build()
        }
        startForeground(1, notification)

    }

    private fun getPendingIntent(context : Context,  action : Int): PendingIntent? {
        val intent = Intent(this, MyReceiver::class.java)
        intent.putExtra(Constance.PUT_EXTRA_ACTION_MUSIC, action)
        return PendingIntent.getBroadcast(context, action, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
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
    private fun handActionMusic(action : Int) {
        when(action) {
            Constance.ACTION_PAUSE -> {
                pauseMusic()
                sendNotification(mSong)
                sendActionToActivity(Constance.ACTION_PAUSE)

            }
            ACTION_RESUME -> {
                resumeMusic()
                sendNotification(mSong)
                sendActionToActivity(Constance.ACTION_RESUME)

            }
            ACTION_CLEAR -> {
                stopSelf()
                sendNotification(mSong)
                sendActionToActivity(Constance.ACTION_CLEAR)

            }
        }
    }

    private fun resumeMusic() {
        if(mediaPlayer != null && !isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    private fun pauseMusic() {
        if(mediaPlayer != null && isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
    }

    private fun sendActionToActivity(action : Int) {
        val intent = Intent(Constance.SEND_DATA_TO_ACTIVITY)
        val bundel = Bundle()
        bundel.putParcelable(Constance.PUT_PARCELABLE_SONG, mSong)
        bundel.putBoolean(Constance.PUT_PARCElABLE_ACTIVE, isPlaying)
        bundel.putInt(Constance.PUT_PARCELABLE_STATE, action)
        intent.putExtras(bundel)


        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}