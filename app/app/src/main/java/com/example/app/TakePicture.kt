package com.example.app

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

class TakePicture : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)
        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val product_id = intent.getStringExtra("product_id") as String
            val file = bitmapToFile(imageBitmap, product_id)
            val api = API(this)
            Executors.newSingleThreadExecutor().execute{
                api.sendPicture(file, product_id)
            }
            val intent = Intent(this@TakePicture, ListRating::class.java)
            startActivity(intent)
        }
    }

    fun bitmapToFile(bitmap: Bitmap, product_id: String): File {

        //create a file to write bitmap data
        val file = File(this.getCacheDir(), "$product_id.png")
        file.createNewFile()

        //Convert bitmap to byte array
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, outputStream)
        val byteData = outputStream.toByteArray()

        //write the bytes in file
        val fileOutputStream = FileOutputStream(file);
        fileOutputStream.write(byteData)
        fileOutputStream.flush()
        fileOutputStream.close()
        return file
    }
}

