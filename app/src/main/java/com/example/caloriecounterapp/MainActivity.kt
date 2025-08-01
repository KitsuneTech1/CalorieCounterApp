package com.example.caloriecounterapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private var calorieGoal = 0
    private var caloriesRemaining = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etCalorieGoal: EditText = findViewById(R.id.et_calorie_goal)
        val etCaloriesConsumed: EditText = findViewById(R.id.et_calories_consumed)
        val btnAddCalories: Button = findViewById(R.id.btn_add_calories)
        val tvCaloriesRemaining: TextView = findViewById(R.id.tv_calories_remaining)

        tvCaloriesRemaining.text = "Set a goal to start"

        btnAddCalories.setOnClickListener {
            val goalStr = etCalorieGoal.text.toString()
            val consumedStr = etCaloriesConsumed.text.toString()

            if (goalStr.isNotEmpty() && calorieGoal == 0) {
                calorieGoal = goalStr.toInt()
                caloriesRemaining = calorieGoal
                etCalorieGoal.isEnabled = false // Lock the goal after setting it
                tvCaloriesRemaining.text = "Calories remaining: $caloriesRemaining"
                etCaloriesConsumed.text.clear()
                etCaloriesConsumed.requestFocus()
            }

            if (consumedStr.isNotEmpty()) {
                val consumed = consumedStr.toInt()
                caloriesRemaining -= consumed
                tvCaloriesRemaining.text = "Calories remaining: $caloriesRemaining"
                etCaloriesConsumed.text.clear()
            } else {
                if (calorieGoal != 0) {
                    Toast.makeText(this, "Please enter calories consumed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
