package com.example.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // todo if authenticated -> main page <void> for now
        // todo if not authenticated -> connexion page
        // todo on authentication page -> register link
    }
}
