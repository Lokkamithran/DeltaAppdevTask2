package com.example.pong

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_second)

        val pong = findViewById<GameView>(R.id.pong)
        val flag = intent.getIntExtra("flag", 0)

        if(flag == 1) pong.hardMode = true

            pong.startGame()
    }
}