package com.example.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.rating_list_row.view.*
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
        val rowView = inflater.inflate(R.layout.rating_list_row, parent, false)
        val rating = getItem(position) as Rating

        /* Row View : Show Item */
        rowView.nameFieldTxt.text = rating.product.name
        rowView.gradeFieldTxt.text = rating.value
        rowView.dateFieldTxt.text = rating.date?.substring(0, 10)

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
            val intent = Intent(this.context, ProductDetail::class.java)
            intent.putExtra("rating", rating)
            this.context.startActivity(intent)
        }

        return rowView
    }
}


class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /* List View : Add Item */
        val addRatingBtn = findViewById<Button>(R.id.addRatingBtn)
        addRatingBtn.setOnClickListener {
            val intent = Intent(this@Home, AddProductRating::class.java)
            startActivity(intent)
        }

        /* List View : Initialization */
        Executors.newSingleThreadExecutor().execute {
            val api = API(this)
            val ratings = api.getRatings()!!
            val adapter = RatingAdapter(this, ratings)
            runOnUiThread { ratingListView.adapter  = adapter }
        }

    }
}
