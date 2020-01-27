package com.example.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        // todo catch button message (username and password)
        // todo send it to django backend
        // todo check response and send invalid screen / log in screen
    }
}
