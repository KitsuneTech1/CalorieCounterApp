package com.example.caloriecounterapp

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.caloriecounterapp.databinding.ActivitySettingsBinding
import java.util.Calendar

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReset.setOnClickListener {
            val prefs = getSharedPreferences("CalorieCounterPrefs", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.clear()
            editor.apply()

            val intent = Intent()
            intent.putExtra("reset", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        binding.btnSetResetTime.setOnClickListener {
            showTimePickerDialog()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showTimePickerDialog() {
        val prefs = getSharedPreferences("CalorieCounterPrefs", Context.MODE_PRIVATE)
        val resetHour = prefs.getInt("resetHour", 0)
        val resetMinute = prefs.getInt("resetMinute", 0)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val editor = prefs.edit()
                editor.putInt("resetHour", hourOfDay)
                editor.putInt("resetMinute", minute)
                editor.apply()
                updateResetTimeButtonText(hourOfDay, minute)
            },
            resetHour,
            resetMinute,
            false
        )
        timePickerDialog.show()
    }

    private fun updateResetTimeButtonText(hour: Int, minute: Int) {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = if (hour == 0 || hour == 12) 12 else hour % 12
        binding.btnSetResetTime.text = String.format("Reset Time: %d:%02d %s", displayHour, minute, amPm)
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("CalorieCounterPrefs", Context.MODE_PRIVATE)
        val resetHour = prefs.getInt("resetHour", 0)
        val resetMinute = prefs.getInt("resetMinute", 0)
        updateResetTimeButtonText(resetHour, resetMinute)
    }
}
