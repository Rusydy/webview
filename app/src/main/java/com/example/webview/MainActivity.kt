package com.example.webview

import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import android.os.Bundle

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val imageView2 = findViewById<ImageView>(R.id.imageView2)
        val textView = findViewById<TextView>(R.id.textView)

        // Load Image from URL using Glide and resize ImageView
        val imageUrl = "https://d1jnx9ba8s6j9r.cloudfront.net/blog/wp-content/uploads/2019/06/Kotlin.png"
        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        // Load Image from URL using Glide and resize ImageView
        val imageUrl2 = "https://res.cloudinary.com/andresbaravalle/image/upload/v1542975073/logo-javascript_2x_cpugmu.png"
        Glide.with(this)
            .load(imageUrl2)
            .into(imageView2)

        textView.text = "Text you want to make copyable"
    }
}
