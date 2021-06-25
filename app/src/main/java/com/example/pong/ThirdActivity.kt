package com.example.pong

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ThirdActivity : AppCompatActivity() {

    private val SHARED_PREF = "sharedPrefs"
    private val SCORE = "score"
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.popout_layout)


        val flag = intent.getIntExtra("flag2", 0)
        val scoreText = findViewById<TextView>(R.id.game_over)
        val restartButton = findViewById<Button>(R.id.restart_button)
        val menuButton = findViewById<Button>(R.id.menu_button)

        scoreText.text = getString(R.string.game_over_text, flag)
        restartButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("flag", 1)
            startActivity(intent)
        }
        menuButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        loadData()
        if(flag>score) {
            score = flag
            saveData()
        }

    }
    private fun saveData(){
        val sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt(SCORE, score)
        editor.apply()
    }

    private fun loadData(){
        val sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)
        score = sharedPreferences.getInt(SCORE, 0)
    }
}