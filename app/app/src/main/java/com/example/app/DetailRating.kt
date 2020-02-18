package com.example.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import kotlinx.android.synthetic.main.activity_detail_rating.*
import java.lang.Float.parseFloat

class DetailRating : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_rating)

        /* Show Rating Detail from Row View */
        val rating = intent.getSerializableExtra("rating") as Rating
        /* Required */
        nameTxt.text = rating.product.name
        ratingBar.rating = parseFloat(rating.value)
        /* Autofilled */
        dateTxt.text = rating.date ?: "-"
        /* Optional */
        commentTxt.text = rating.comment ?: "..."
        storeTxt.text = rating.product.store ?: "-"
        yearTxt.text = rating.product.year ?: "-"
        typeTxt.text = rating.product.type ?: "-"
        volTxt.text = rating.product.vol ?: "-"


        /* Edit Button */
        editBtn.setOnClickListener {
            val intent = Intent(this@DetailRating, EditRating::class.java)
            intent.putExtra("rating", rating)
            startActivity(intent)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            val intent = Intent(this@DetailRating, ListRating::class.java)
            startActivity(intent)
        }
        return super.onKeyDown(keyCode, event)
    }
}
