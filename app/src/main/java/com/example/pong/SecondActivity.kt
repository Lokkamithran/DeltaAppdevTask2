package com.example.pong

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.activity_second)

        val displayMetrics = DisplayMetrics()
        display?.getRealMetrics(displayMetrics)

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val pong = findViewById<GameView>(R.id.pong)
        val flag = intent.getIntExtra("flag", 0)

        pong.circleX = ((width/3)..(2*width/3)).random().toFloat()
        pong.circleY = if((height/20 + 60)<height/2) ((height/20 + 60)..height/2).random().toFloat()
            else (0..height/2).random().toFloat()
        if(flag == 1) pong.hardMode = true

            pong.startGame()
    }
}