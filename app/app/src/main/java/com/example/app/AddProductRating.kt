package com.example.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_product_rating.*
import java.util.concurrent.Executors

class AddProductRating : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product_rating)

        validateBtn.setOnClickListener {
            val name = nameField.text.toString()
            val year = yearField.text.toString()
            val store = storeField.text.toString()
            val grade = ratingField.text.toString()
            val product = Product(name=name, store=store, year=year, type=null)
            val rating = Rating(product, grade, date=null)

            Executors.newSingleThreadExecutor().execute{
                val api = API(this)
                api.addRating(rating)
                runOnUiThread {
                    val intent = Intent(this@AddProductRating, Home::class.java)
                    startActivity(intent)
                }
            }

        }
    }
}
