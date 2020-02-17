package com.example.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Half.toFloat
import kotlinx.android.synthetic.main.activity_product_detail.*
import java.lang.Float.parseFloat

class ProductDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

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
    }
}
