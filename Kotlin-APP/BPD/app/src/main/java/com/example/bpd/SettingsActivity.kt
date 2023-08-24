package com.example.bpd

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
class SettingsActivity : AppCompatActivity() {

    private lateinit var seekBarRoi1Size: SeekBar
    private lateinit var switchOverlay: SwitchCompat
    private lateinit var editTextRoi1X: EditText
    private lateinit var editTextRoi1Y: EditText
    private lateinit var textViewSeekBarMin: TextView
    private lateinit var textViewSeekBarMax: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        seekBarRoi1Size = findViewById(R.id.seekBarRoi1Size)
        switchOverlay = findViewById(R.id.switchOverlay)
        editTextRoi1X = findViewById(R.id.editTextRoi1X)
        editTextRoi1Y = findViewById(R.id.editTextRoi1Y)
        textViewSeekBarMin = findViewById(R.id.textViewSeekBarMin)
        textViewSeekBarMax = findViewById(R.id.textViewSeekBarMax)

        val buttonSave: Button = findViewById(R.id.buttonSave)

        // Load existing settings or use default values
        val sharedPref = getSharedPreferences(getString(R.string.settings_shared_pref), MODE_PRIVATE)
        val roi1Size = sharedPref.getInt(getString(R.string.settings_roi1_size), 100)
        val isOverlayEnabled = sharedPref.getBoolean(getString(R.string.settings_overlay_enabled), true)
        val roi1X = sharedPref.getInt(getString(R.string.settings_roi1_x), 180)
        val roi1Y = sharedPref.getInt(getString(R.string.settings_roi1_y), 320)
        val minSeekBarValue = 100
        val maxSeekBarValue = seekBarRoi1Size.max

        seekBarRoi1Size.progress = roi1Size
        switchOverlay.isChecked = isOverlayEnabled
        editTextRoi1X.setText(roi1X.toString())
        editTextRoi1Y.setText(roi1Y.toString())
        textViewSeekBarMin.text = minSeekBarValue.toString()
        textViewSeekBarMax.text = maxSeekBarValue.toString()

        buttonSave.setOnClickListener {
            // Save settings
            val editor = sharedPref.edit()
            editor.putInt(getString(R.string.settings_roi1_size), seekBarRoi1Size.progress)
            editor.putBoolean(getString(R.string.settings_overlay_enabled), switchOverlay.isChecked)
            editor.putInt(getString(R.string.settings_roi1_x), editTextRoi1X.text.toString().toInt())
            editor.putInt(getString(R.string.settings_roi1_y), editTextRoi1Y.text.toString().toInt())
            editor.apply()

            finish()
        }
    }
}

