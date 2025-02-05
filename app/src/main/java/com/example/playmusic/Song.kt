package com.example.playmusic

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(val title: String, val single: String, val image: Int, val resource: Int) : Parcelable