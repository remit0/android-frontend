package com.example.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list_rating.*
import kotlinx.android.synthetic.main.list_rating_row.view.*
import java.util.concurrent.Executors


class RatingAdapter(
    private val context: Context,
    private var dataSource: MutableList<Rating>
) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
            as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.list_rating_row, parent, false)
        val rating = getItem(position) as Rating

        /* Row View : Show Item */
        rowView.nameFieldTxt.text = rating.product.name
        rowView.gradeFieldTxt.text = rating.value
        rowView.dateFieldTxt.text = rating.date?.substring(0, 10)

        var img: Bitmap? = null
        Executors.newSingleThreadExecutor().execute {
            val api = API(rowView.context)
            img = api.getPicture(rating.product.id!!)
            img?.let { rowView.imageView.setImageBitmap(img) }
        }

        /* Row View : Delete Item */
        rowView.deleteRatingBtn.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                val api = API(rowView.context)
                api.deleteRating(rating.id!!)
            }
            dataSource.removeAt(position)
            notifyDataSetChanged()
        }

        /* Row View : On Click */
        rowView.setOnClickListener {
            val intent = Intent(this.context, DetailRating::class.java)
            intent.putExtra("rating", rating)
            this.context.startActivity(intent)
        }

        /* Row View Image : On Click */
        rowView.imageView.setOnClickListener {
            val intent = Intent(this.context, TakePicture::class.java)
            println(rating.product.id)
            intent.putExtra("product_id", rating.product.id)
            this.context.startActivity(intent)
        }

        return rowView
    }
}


class ListRating : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_rating)

        /* List View : Add Item */
        val addRatingBtn = findViewById<Button>(R.id.addRatingBtn)
        addRatingBtn.setOnClickListener {
            val intent = Intent(this@ListRating, AddRating::class.java)
            startActivity(intent)
        }

        /* List View : Initialization */
        Executors.newSingleThreadExecutor().execute {
            val api = API(this)
            val ratings = api.getRatings()!!
            val ratingsSorted = ratings.sortedByDescending { it.date }.toMutableList()
            val adapter = RatingAdapter(this, ratingsSorted)
            runOnUiThread { ratingListView.adapter  = adapter }
        }

    }
}
