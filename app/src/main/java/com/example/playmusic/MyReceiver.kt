package com.example.playmusic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var actionMusic = intent?.getIntExtra(Constance.PUT_EXTRA_ACTION_MUSIC, 0)
        val intentService = Intent(context, MyService::class.java)
        intentService.putExtra(Constance.PUT_EXTRA_ACTION_MUSIC_SERVICE, actionMusic)
        context?.startService(intentService)
    }
}