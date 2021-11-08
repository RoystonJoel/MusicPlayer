package com.example.musicplayer

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
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
import java.io.File
import java.util.jar.Manifest

private val PERMISSION_REQUEST = 10
class MainActivity : AppCompatActivity() {
    private var permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!checkPermission(applicationContext, permissions)) {
                    requestPermissions(permissions, PERMISSION_REQUEST)
                }
            }

        val button1 = findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    initData()
                }
        }
    }



    @RequiresApi(Build.VERSION_CODES.R)
    private fun initData(){
        Log.i("getcontents", "i am here")
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val c = contentResolver.query(uri,null, selection,null,null)
        if (c != null)
        {
            Log.i("getcontents", c.count.toString())
            while (c.moveToNext()){
                val Url = c.getString(c.getColumnIndex(MediaStore.Audio.Media._ID))
                val author = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                Log.i("getcontents", "HELLO???")
            }
            c.close()
        }
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