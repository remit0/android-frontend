package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import java.util.concurrent.Executors


class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val submitBtn = findViewById<Button>(R.id.submitBtn)

        submitBtn.setOnClickListener{
            val username = usernameTxt.text.toString()
            val password = passwordTxt.text.toString()

            Executors.newSingleThreadExecutor().execute{
                val api = API(this)
                var status = api.register(username, password)

                if (status){

                    status = api.login(username, password)

                    if (status) {
                        val intent = Intent(this@Register, ListRating::class.java)
                        startActivity(intent)

                    } else {
                        runOnUiThread {
                            usernameTxt.text.clear()
                            passwordTxt.text.clear()
                        }
                    }

                } else {
                    runOnUiThread {
                        usernameTxt.text.clear()
                        passwordTxt.text.clear()
                    }
                }
            }
        }
    }
}
