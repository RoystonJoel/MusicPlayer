package com.example.musicplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet(val item: Audio) :BottomSheetDialogFragment(){

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.coordinator_layout,container,false)
        val play_pause_Button = v.findViewById<ImageButton>(R.id.play_pause_Button)
        val next_Button = v.findViewById<ImageButton>(R.id.next_Button)
        val prev_Button = v.findViewById<ImageButton>(R.id.prev_Button)
        val fav_Button = v.findViewById<ImageButton>(R.id.fav_Button)
        val loop_Button = v.findViewById<ImageButton>(R.id.loop_Button)
        val seekBar = v.findViewById<SeekBar>(R.id.seekBar)
        val Artist_view = v.findViewById<ImageView>(R.id.Artist_view)




        return v
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}