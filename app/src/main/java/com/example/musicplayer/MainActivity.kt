package com.example.musicplayer

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.AudioAttributes
import android.media.Image
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File
import java.util.jar.Manifest


private val PERMISSION_REQUEST = 10
class MainActivity : AppCompatActivity() {
    private var permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var adapter: TheAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    val mediaPlayer = MediaPlayer().apply {
        AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    }

    private fun showDetail(item: Audio) {

        if (mediaPlayer.isPlaying)
        {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        mediaPlayer.setDataSource(item.uri)
        mediaPlayer.prepare()
        mediaPlayer.start()

        BottomSheetBehavior.from(findViewById(R.id.sheet)).apply{
            peekHeight=200
            state=BottomSheetBehavior.STATE_COLLAPSED

        }
    }



    @RequiresApi(Build.VERSION_CODES.R)
    private fun initData(): List<Audio>{
        val list = mutableListOf<Audio>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val c = contentResolver.query(uri,null, null,null,null)
        if (c != null)
        {
            while (c.moveToNext()){
                var index: Int = c.getInt(0)
                val _uri = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA))
                val _id = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                val title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val author = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val _duration = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DURATION))


                Log.i("show",title)
                Log.i("show",uri.toString())
                Log.i("show",_id.toString())
                Log.i("show","---------")


                list.add(Audio(_uri,_id,title,author,_duration))
            }
            c.close()
        }
        return list
    }



    fun checkPermission(context: Context,permissionArray: Array<String>): Boolean{
        var allSucess = true
        for (i in permissionArray.indices){
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED){
                allSucess = false
            }
        }
        return allSucess
    }


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