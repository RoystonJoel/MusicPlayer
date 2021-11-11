package com.example.musicplayer

import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import java.util.*

class TheAdapter(private val data: List<Audio>, private val listener: (Audio) -> Unit) : RecyclerView.Adapter<TheAdapter.TheHolder>() {

    lateinit var parent_temp: ViewGroup
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TheAdapter.TheHolder {
        parent_temp = parent
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
                .inflate(R.layout.layout_row, parent, false) as View
        return TheHolder(view)
    }

    override fun getItemCount() = data.size

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: TheAdapter.TheHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class TheHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val AlbumCover: ImageView = v.findViewById(R.id.AlbumCover)
        private val _title: TextView = v.findViewById(R.id.Title_view)
        private val _Artist: TextView = v.findViewById(R.id.Artist_view)
        private val _duration: TextView = v.findViewById(R.id.duration_view)

        @RequiresApi(Build.VERSION_CODES.R)
        fun bind(item: Audio) {



            //Glide.with(parent_temp).load(item.uri).placeholder(R.drawable.ic_baseline_music_note_24).into(AlbumCover)
            _title.text = item._title
            _Artist.text = item.author
            _duration.text = item._duration
            v.setOnClickListener { listener(item) }
        }

    }
}