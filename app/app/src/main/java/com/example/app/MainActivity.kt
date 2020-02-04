package com.example.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val token = TokenHandler(this).read()
        val intent = if (token == null) Intent(this@MainActivity, Login::class.java)
            else Intent(this@MainActivity, Home::class.java)
        startActivity(intent)
    }
}
