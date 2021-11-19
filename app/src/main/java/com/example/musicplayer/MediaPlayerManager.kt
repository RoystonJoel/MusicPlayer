package com.example.musicplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.annotation.RequiresApi

class MediaPlayerManager {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val mediaPlayer = MediaPlayer().apply {
        AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
    }

    fun getduration(): Int{
        return mediaPlayer.duration
    }

    fun play(){
        mediaPlayer.start()
    }

    fun pause(){
        mediaPlayer.pause()
    }

    fun stop(){
        mediaPlayer.stop()
    }

    fun isPlaying(): Boolean{
        return mediaPlayer.isPlaying
    }

    fun seekTo(progress: Int){
        return mediaPlayer.seekTo(progress)
    }

    fun currentPosition(): Int{
        return mediaPlayer.currentPosition
    }

    fun OnCompletionListener(playPauseButton: ImageButton){
        mediaPlayer.setOnCompletionListener {
            mediaPlayer.pause()
            playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    fun setDataSource(temp_item: Audio,seekbar: SeekBar) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(temp_item.uri)
        mediaPlayer.prepare()
        seekbar.max = getduration()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
        }
    }


}