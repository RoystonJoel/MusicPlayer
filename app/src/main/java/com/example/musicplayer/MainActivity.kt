package com.example.musicplayer

import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior



private const val PERMISSION_REQUEST = 10
class MainActivity : AppCompatActivity() {
    private var permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var adapter: TheAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var sheet: View
    private lateinit var runnable: Runnable
    private var handler = Handler()
    private lateinit var seekbar: SeekBar
    private var currentSongID = 0
    private val mediaPlayerManager = MediaPlayerManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            //Checks User permissions because we are accessing external storage, if there are none than we request it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!checkPermission(applicationContext, permissions)) {
                    requestPermissions(permissions, PERMISSION_REQUEST)
                }
            }

        val listView = findViewById<RecyclerView>(R.id.Audio_list)

        linearLayoutManager = LinearLayoutManager(this)
        listView.layoutManager = linearLayoutManager

        lateinit var data: List<Audio>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            data = initData()
        }

        adapter = TheAdapter(data) { showDetail(it) }
        listView.adapter = adapter

        sheet = findViewById<FrameLayout>(R.id.sheet)
        BottomSheetBehavior.from(sheet).apply {
            peekHeight=300
            state=BottomSheetBehavior.STATE_HIDDEN
        }

        val shuffleButton = findViewById<ImageButton>(R.id.shuffleButton)
        val prevButton = findViewById<ImageButton>(R.id.prev_Button)
        val playPauseButton = findViewById<ImageButton>(R.id.play_pause_Button)
        val nextButton = findViewById<ImageButton>(R.id.next_Button)
        val loopButton = findViewById<ImageButton>(R.id.loop_Button)
        seekbar = findViewById(R.id.seekBar)


        shuffleButton.setOnClickListener {

        }
        prevButton.setOnClickListener {
            playprev(data)
        }
        playPauseButton.setOnClickListener {
            if (mediaPlayerManager.isPlaying()){
                mediaPlayerManager.pause()
                playPauseButton.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            } else {
                mediaPlayerManager.play()
                playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24)
            }
        }
        nextButton.setOnClickListener {
            playnext(data)
        }

        seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed
                if (fromUser){
                    mediaPlayerManager.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                mediaPlayerManager.pause()
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                mediaPlayerManager.play()
            }
        })

        runnable = Runnable {
            seekbar.progress = mediaPlayerManager.currentPosition()
            handler.postDelayed(runnable,1000)
        }
        handler.postDelayed(runnable,1000)
        mediaPlayerManager.OnCompletionListener(playPauseButton)
    }

    fun playnext(data: List<Audio>){
        var tempId = 0
        data.forEach {
            if (it._id == currentSongID)
            {
                tempId = it._id + 1

            }
        }
        data.find{it._id==tempId}?.let {
            mediaPlayerManager.setDataSource(it,seekbar)
            setArtView(it)
        }
        currentSongID = tempId
        seekbar.max = mediaPlayerManager.getduration()

        Log.i("next/prev",tempId.toString())
    }

    fun playprev(data: List<Audio>){
        var tempId = 0
        data.forEach {
            if (it._id == currentSongID)
            {
                tempId = it._id - 1

            }
        }
        data.find{it._id==tempId}?.let {
            mediaPlayerManager.setDataSource(it,seekbar)
            setArtView(it)
        }
        currentSongID = tempId
        seekbar.max = mediaPlayerManager.getduration()
        Log.i("next/prev",tempId.toString())
    }


    // when a user clicks on a row the following is executed
    // we save the ID of the current song that is playing and if one
    // is already playing we stop it and play the one the user just clicked by setting the Data source of the song
    //
    private fun showDetail(item: Audio) {
        currentSongID = item._id
        if (!item.Islastrow)
        {
            if (mediaPlayerManager.isPlaying())
            {
                mediaPlayerManager.stop()
            }
                mediaPlayerManager.setDataSource(item,seekbar)
            val playPauseButton = findViewById<ImageButton>(R.id.play_pause_Button)
            playPauseButton.setImageResource(R.drawable.ic_baseline_pause_24)

            BottomSheetBehavior.from(sheet).apply {
                peekHeight=300
                state=BottomSheetBehavior.STATE_COLLAPSED
            }
            setArtView(item)
        }
    }

    //sets the imageview in the bottomsheet with the album Art
    private fun setArtView(item: Audio){
        val itemBitmap = item.getAlbumCover()
        val artview = findViewById<ImageView>(R.id.art_View)
        if (itemBitmap == null){
            artview.setImageResource(R.drawable.ic_baseline_music_note_24)
        }else{
            artview.setImageBitmap(item.getAlbumCover())
            artview.adjustViewBounds = true
        }
    }



    //checks external storage for Music and gets the URI, title,author and gives them an ID as well, we
    // also store in some dummy rows at the end of the array so there is a gap at the end of the screen.
    @RequiresApi(Build.VERSION_CODES.R)
    private fun initData(): List<Audio>{
        val list = mutableListOf<Audio>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val c = contentResolver.query(uri,null, null,null,null)
        if (c != null)
        {
            var i = 1
            while (c.moveToNext()){
                val _uri = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA))
                val title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val author = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST))

                list.add(Audio(i,_uri,title,author,false))
                i++
            }
            c.close()

            val dummy = ""
            var k = 1
            while (k>=0)
            {
                list.add(Audio(0,dummy,dummy,dummy,true))
                k--
            }
        }
        return list
    }


    //checks permission
    fun checkPermission(context: Context,permissionArray: Array<String>): Boolean{
        var allSucess = true
        for (i in permissionArray.indices){
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED){
                allSucess = false
            }
        }
        return allSucess
    }

    //requests permission
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST){
            var allSuccess = true
            for(i in permissions.indices){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    allSuccess = false
                    var requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain){
                        Toast.makeText(applicationContext,"Permission denied",Toast.LENGTH_LONG).show()
                    } else{
                        Toast.makeText(applicationContext,"please enable permissions",Toast.LENGTH_LONG).show()
                    }
                }
            }
            if(allSuccess){
                Toast.makeText(applicationContext,"Permission Granted",Toast.LENGTH_LONG).show()
            }
        }
    }


}