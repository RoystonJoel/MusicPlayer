package com.example.musicplayer

import java.net.URI

 data class Audio(val uri: URI,
    val name: String,
      val duration: Int,
     val size: Int){
}