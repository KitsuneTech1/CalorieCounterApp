package com.example.caloriecounterapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.caloriecounterapp.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences
    private var calorieGoal = 0
    private var caloriesRemaining = 0

    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null && data.getBooleanExtra("reset", false)) {
                resetCalorieGoal()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("CalorieCounterPrefs", Context.MODE_PRIVATE)
        loadData()
        checkForMidnightReset()

        binding.btnAddCalories.setOnClickListener {
            handleCalorieUpdate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                settingsLauncher.launch(intent)
                true
            }
            R.id.action_history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleCalorieUpdate() {
        if (calorieGoal == 0) {
            // Saving the calorie goal
            val goalStr = binding.etCalorieGoal.text?.toString() ?: ""
            if (goalStr.isNotEmpty()) {
                try {
                    calorieGoal = goalStr.toInt()
                    caloriesRemaining = calorieGoal
                    saveData()
                    updateUI()
                    binding.etCaloriesConsumed.requestFocus()
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid number for the goal", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a calorie goal", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Subtracting consumed calories
            val consumedStr = binding.etCaloriesConsumed.text?.toString() ?: ""
            if (consumedStr.isNotEmpty()) {
                try {
                    val consumed = consumedStr.toInt()
                    caloriesRemaining -= consumed
                    saveData()
                    updateUI()
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid number for calories", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter calories consumed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetCalorieGoal() {
        calorieGoal = 0
        caloriesRemaining = 0
        binding.etCalorieGoal.text?.clear()
        binding.etCalorieGoal.isEnabled = true
        saveData()
        updateUI()
        Toast.makeText(this, "Calorie goal reset", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI() {
        if (calorieGoal == 0) {
            binding.tvCaloriesRemaining.text = "Set a goal to start"
            binding.etCalorieGoal.isEnabled = true
            binding.tilCaloriesConsumed.visibility = View.GONE
            binding.btnAddCalories.text = "Save Calorie Goal"
        } else {
            binding.tvCaloriesRemaining.text = "Calories remaining: $caloriesRemaining"
            binding.etCalorieGoal.setText(calorieGoal.toString())
            binding.etCalorieGoal.isEnabled = false
            binding.tilCaloriesConsumed.visibility = View.VISIBLE
            binding.btnAddCalories.text = "Subtract Calories"
        }
        binding.etCaloriesConsumed.text?.clear()
    }

    private fun saveData() {
        val editor = prefs.edit()
        editor.putInt("calorieGoal", calorieGoal)
        editor.putInt("caloriesRemaining", caloriesRemaining)
        editor.putLong("lastOpened", System.currentTimeMillis())
        editor.apply()
    }

    private fun loadData() {
        calorieGoal = prefs.getInt("calorieGoal", 0)
        caloriesRemaining = prefs.getInt("caloriesRemaining", 0)
        updateUI()
    }

    private fun checkForMidnightReset() {
        val lastOpened = prefs.getLong("lastOpened", 0)
        if (lastOpened == 0L) return

        val resetHour = prefs.getInt("resetHour", 0)
        val resetMinute = prefs.getInt("resetMinute", 0)

        val lastCal = Calendar.getInstance().apply { timeInMillis = lastOpened }
        val currentCal = Calendar.getInstance()

        val resetCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, resetHour)
            set(Calendar.MINUTE, resetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (currentCal.after(resetCal) && lastCal.before(resetCal)) {
            // Save the previous day's history
            val historySet = prefs.getStringSet("history", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            val dateStr = "${yesterday.get(Calendar.MONTH) + 1}/${yesterday.get(Calendar.DAY_OF_MONTH)}/${yesterday.get(Calendar.YEAR)}"
            val totalConsumed = calorieGoal - caloriesRemaining
            historySet.add("$dateStr: $totalConsumed / $calorieGoal calories")
            prefs.edit().putStringSet("history", historySet).apply()

            // Reset for the new day
            caloriesRemaining = calorieGoal
            saveData()
            updateUI()
            Toast.makeText(this, "New day! Calories reset.", Toast.LENGTH_SHORT).show()
        }
    }
}
