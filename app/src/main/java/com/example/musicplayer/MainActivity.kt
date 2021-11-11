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
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
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
    private lateinit var sheet: View




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

        sheet = findViewById<FrameLayout>(R.id.sheet)
        BottomSheetBehavior.from(sheet).apply {
            peekHeight=300
            state=BottomSheetBehavior.STATE_HIDDEN
        }

    }

    private fun showDetail(item: Audio) {
        if (!item.Islastrow)
        {
            item.play()
            BottomSheetBehavior.from(sheet).apply {
                peekHeight=300
                state=BottomSheetBehavior.STATE_COLLAPSED
            }

            val artview = findViewById<ImageView>(R.id.art_View)
            artview.setImageBitmap(item.getAlbumCover())
        }
    }



    @RequiresApi(Build.VERSION_CODES.R)
    private fun initData(): List<Audio>{
        val list = mutableListOf<Audio>()

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
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
                Log.i("show",_uri.toString())
                Log.i("show",_id.toString())
                Log.i("show","---------")


                list.add(Audio(_uri,_id,title,author,_duration,false))
            }
            c.close()

            val dummy = " "
            val dummy3: Long = 0
            list.add(Audio(dummy,dummy3,dummy,dummy,dummy,true))
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