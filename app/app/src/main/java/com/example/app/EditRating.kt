package com.example.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_edit_rating.*
import kotlinx.android.synthetic.main.activity_edit_rating.commentTxt
import kotlinx.android.synthetic.main.activity_edit_rating.dateTxt
import kotlinx.android.synthetic.main.activity_edit_rating.nameTxt
import kotlinx.android.synthetic.main.activity_edit_rating.ratingBar
import java.lang.Float.parseFloat
import java.util.concurrent.Executors

class EditRating : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_rating)

        /* Get Rating Detail from Row View */
        var rating = intent.getSerializableExtra("rating") as Rating

        /* Set default values */
        // name
        nameTxt.setText(rating.product.name)

        // rating
        ratingBar.rating = parseFloat(rating.value)

        // store
        val storeAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, storeChoices
        )
        spinnerStore.adapter = storeAdapter
        spinnerStore.setSelection(storeChoices.indexOf(rating.product.store))

        // year
        val yearChoices = getYearChoices()
        val yearAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, yearChoices
        )
        spinnerYear.adapter = yearAdapter
        spinnerYear.setSelection(yearChoices.indexOf(rating.product.year))

        // type
        val typeAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, typeChoices
        )
        spinnerType.adapter = typeAdapter
        spinnerType.setSelection(typeChoices.indexOf(rating.product.type))

        // date
        dateTxt.text = rating.date ?: "-"

        // vol
        volBar.progress = parseFloat(rating.product.vol!!).toInt()

        // comment
        commentTxt.setText(rating.comment ?: "...")


        /* Edit button */
        editBtn.setOnClickListener {
            /* gather new parameters */
            val name = nameTxt.text.toString()
            val value = ratingBar.rating.toString()
            val store = spinnerStore.selectedItem.toString()
            val year = spinnerYear.selectedItem.toString()
            val type = spinnerType.selectedItem.toString()
            val vol = volBar.progress.toString()
            val comment = commentTxt.text.toString()

            rating.product.name = name
            rating.value = value
            rating.product.store = store
            rating.product.year = year
            rating.product.type = type
            rating.product.vol = vol
            rating.comment = comment


            Executors.newSingleThreadExecutor().execute{
                /* make API call */
                val api = API(this)
                api.updateRating(rating.id!!, rating.serialize())

                /* redirect view */
                runOnUiThread {
                    val intent = Intent(this@EditRating, DetailRating::class.java)
                    intent.putExtra("rating", rating)
                    startActivity(intent)
                }
            }
        }
    }
}
