package com.example.app

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.rating_list_row.view.*
import java.util.concurrent.Executors


class RatingAdapter(private val context: Context,
                    private val dataSource: Array<Rating>) : BaseAdapter() {

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
        val item = getItem(position) as Rating
        rowView.text1RatingRow.text = item.rating.toString()
        return rowView
    }
}


class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val addRatingBtn = findViewById<Button>(R.id.addRatingBtn)
        addRatingBtn.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                val api = API(this)
                val new_rating = Rating(product=2, rating=2, user=null, date=null)
                api.addRating(new_rating)
            }
        }

        Executors.newSingleThreadExecutor().execute {
            val api = API(this)
            val ratings = api.getRatings()!!
            val listView = findViewById<ListView>(R.id.ratingListView)
            val adapter = RatingAdapter(this, ratings)
            runOnUiThread{ listView.adapter = adapter }
        }
    }
}
