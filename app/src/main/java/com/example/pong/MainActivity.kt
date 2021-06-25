package com.example.pong

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var play: Button
    private val SHARED_PREF = "sharedPrefs"
    private val SCORE = "score"
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_main)

        val scoreText = findViewById<TextView>(R.id.score_text)
        loadData()
        scoreText.text = getString(R.string.high_score, score)

        play = findViewById(R.id.play_button)
        play.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("flag", 1)
            startActivity(intent)
        }

    }

    private fun loadData() {
        val sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        score = sharedPreferences.getInt(SCORE, 0)
    }
}