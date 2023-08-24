package com.example.bpd

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoPickerActivity : AppCompatActivity() {

    private val REQUEST_CODE_SELECT_VIDEO = 1001

    private lateinit var videoPreview: VideoView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAnalyze: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_picker)

        videoPreview = findViewById(R.id.videoPreview)
        progressBar = findViewById(R.id.progressBar)
        btnAnalyze = findViewById(R.id.btnAnalyze)

        val btnPickVideo: Button = findViewById(R.id.btnPickVideo)
        btnPickVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "video/*"
            startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO)
        }

        btnAnalyze.setOnClickListener {

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_VIDEO && resultCode == RESULT_OK) {
            val selectedVideoUri: Uri? = data?.data
            if (selectedVideoUri != null) {
                videoPreview.setVideoURI(selectedVideoUri)
                videoPreview.start()

                progressBar.visibility = ProgressBar.GONE
                btnAnalyze.visibility = Button.VISIBLE
            }
        }
    }
}
