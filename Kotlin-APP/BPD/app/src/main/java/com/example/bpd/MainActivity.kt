package com.example.bpd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPage1: Button = findViewById(R.id.btnPage1)
        val btnPage2: Button = findViewById(R.id.btnPage2)
        val btnPage3: Button = findViewById(R.id.btnPage3)

        btnPage1.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        btnPage2.setOnClickListener {
            val intent = Intent(this, RecentsActivity::class.java)
            startActivity(intent)
        }

        btnPage3.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
