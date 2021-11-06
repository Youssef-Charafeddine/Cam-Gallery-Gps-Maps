package com.youssef

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.content.pm.PackageManager
import android.content.Intent
import android.widget.Toast
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import java.io.IOException

class Activity2 : Activity() {

    var imageView: ImageView? = null
    var takePicButton: Button? = null
    var uploadPicButton: Button? = null
    var imageURI: Uri? = null
    private val camRequest = 1
    private val camPermissionCode = 2
    private val galleryIMAGE = 3
    @RequiresApi(Build.VERSION_CODES.M)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)
        imageView = findViewById(R.id.imageView)
        takePicButton = findViewById(R.id.takePicButton);
        uploadPicButton = findViewById(R.id.uploadPicButton);

        takePicButton?.setOnClickListener(View.OnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), camPermissionCode)
            } else {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, camRequest)
            }
        })
        uploadPicButton?.setOnClickListener(View.OnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(galleryIntent, "Select image"), galleryIMAGE)
        })

        imageURI?.let { contentResolver.notifyChange(it,null) }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == camPermissionCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, camRequest)
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == camRequest && resultCode == RESULT_OK && data != null) {
                val photo = data.extras!!["data"] as Bitmap?
                imageView!!.setImageBitmap(photo)
            }
            if (requestCode == galleryIMAGE && resultCode == RESULT_OK) {
                if (data != null) {
                    imageURI = data.data
                }
                try {
                    val galleryPhoto = MediaStore.Images.Media.getBitmap(contentResolver, imageURI)
                    imageView!!.setImageBitmap(galleryPhoto)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

}