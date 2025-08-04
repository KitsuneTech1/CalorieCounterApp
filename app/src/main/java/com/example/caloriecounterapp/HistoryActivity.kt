package com.example.caloriecounterapp

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.caloriecounterapp.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val prefs = getSharedPreferences("CalorieCounterPrefs", Context.MODE_PRIVATE)
        val historySet = prefs.getStringSet("history", setOf()) ?: setOf()
        val historyList = historySet.toList().sortedDescending()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, historyList)
        binding.lvHistory.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
