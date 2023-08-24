package com.example.bpd

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val backButton: Button = findViewById(R.id.backButton)
        val menuButton: Button = findViewById(R.id.menuButton)
        val resultsTextView: TextView = findViewById(R.id.resultsTextView)





        backButton.setOnClickListener {
            finish() // Finish the activity and go back to the previous activity
        }

        menuButton.setOnClickListener {
            // Handle menu button click
        }

    }
}