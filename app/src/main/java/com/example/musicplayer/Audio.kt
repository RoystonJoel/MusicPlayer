package com.example.musicplayer


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Build
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import kotlin.IllegalArgumentException

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
val mediaPlayer = MediaPlayer().apply {
    AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
}

data class Audio(val uri: String,
                 val _id: Long,
                 val _title: String,
                 val author: String,
                 val _duration: String,
                 val Islastrow: Boolean){


fun getAlbumCover(): Bitmap? {
    val mmr = MediaMetadataRetriever()
    try {
        mmr.setDataSource(uri)

        val data = mmr.embeddedPicture

        if (data != null) {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            return bitmap //associated cover art in bitmap
        }

    } catch (e: IllegalArgumentException) {
        return null
    }
    return null
}

    fun play(){
        if (mediaPlayer.isPlaying)
        {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        mediaPlayer.setDataSource(uri)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }





}