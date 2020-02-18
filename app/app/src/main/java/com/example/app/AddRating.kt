package com.example.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_add_rating.*
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class AddRating : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_rating)

        // configure store drop down list
        val adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, storeChoices
        )
        storeSpinner.adapter = adapter
        storeSpinner.setSelection(0)

        // configure year drop down list
        val yearChoices = getYearChoices()
        val yearAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, yearChoices
        )
        yearSpinner.adapter = yearAdapter
        yearSpinner.setSelection(yearChoices.size - 1)

        // configure type drop down list
        val typeAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, typeChoices
        )
        typeSpinner.adapter = typeAdapter
        typeSpinner.setSelection(0)

        validateBtn.setOnClickListener {
            val name = nameField.text.toString()
            val year = yearSpinner.selectedItem.toString()
            val store = storeSpinner.selectedItem.toString()
            val type = typeSpinner.selectedItem.toString()
            val vol = seekBar.progress.toString()
            val grade = ratingBar.rating.roundToInt().toString()
            val comment = commentTxt.text.toString()

            val product = Product(name=name, store=store, year=year, type=type, vol=vol)
            val rating = Rating(product=product, value=grade, comment=comment)

            Executors.newSingleThreadExecutor().execute{
                val api = API(this)
                api.addRating(rating)
                runOnUiThread {
                    val intent = Intent(this@AddRating, ListRating::class.java)
                    startActivity(intent)
                }
            }

        }
    }
}
