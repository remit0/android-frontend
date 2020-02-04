package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.Executors


class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val submitBtn = findViewById<Button>(R.id.submitBtn)

        submitBtn.setOnClickListener {
            val username = usernameTxt.text.toString()
            val password = passwordTxt.text.toString()

            Executors.newSingleThreadExecutor().execute {
                val api = API(this)
                val status = api.login(username, password)

                if (status) {
                    val intent = Intent(this@Login, Home::class.java)
                    startActivity(intent)
                } else {
                    runOnUiThread {
                        usernameTxt.text.clear()
                        passwordTxt.text.clear()
                    }
                }
            }
        }

        registerRedirectBtn.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }

    }
}
