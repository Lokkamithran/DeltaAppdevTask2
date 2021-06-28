package com.example.pong

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.sqrt


class GameView(context: Context, attributes: AttributeSet): View(context, attributes) {

    private val playerHeight = 60f
    private val playerWidth = 320f
    private var playerX = 0f

    private var touchX = 0f

    private var gameStatus = false

    private val radius = 30f
    var circleX = 0f
    var circleY = 0f
    private var dx = (3..10).random()
    private var dy = sqrt((225-dx*dx).toDouble()).toInt()
    var score = 0

    private val paintBg = Paint().apply {
        color = ContextCompat.getColor(context, R.color.black)
        style = Paint.Style.FILL
    }
    private val paintFill = Paint().apply {
        color = ContextCompat.getColor(context, R.color.orange)
        style = Paint.Style.FILL
    }
    private val paintText = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
        textSize = 300f
    }
    private val paintLine = Paint().apply {
        color = ContextCompat.getColor(context, R.color.white)
        style = Paint.Style.FILL
        strokeWidth = 15f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        playerX = (w/2 - playerWidth/2)
        paintText.textSize = h/20f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBg)
        canvas.drawRect(playerX, height-playerHeight, playerX+playerWidth, height.toFloat(), paintFill)
        canvas.drawCircle(circleX, circleY, radius, paintFill)
        canvas.drawLine(0f, height/20f + 20f, width.toFloat(), height/20f + 20f, paintLine)
        canvas.drawText("SCORE: $score", width/2f - paintText.measureText("SCORE: $score")/2,
            height/20f - 10, paintText)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when(event.action){
            MotionEvent.ACTION_DOWN -> touchX = event.x
            MotionEvent.ACTION_MOVE -> handleMove(event)
        }
        return true
    }

    private fun handleMove(event: MotionEvent){
        playerX -= (touchX - event.x)
        touchX = event.x
        playerX = when {
            playerX<0 -> 0f
            playerX > width - playerWidth -> width - playerWidth
            else -> playerX
        }
        invalidate()
    }
    fun startGame(){
        score = 0
        gameStatus = true
        GameThread().start()
    }

    inner class GameThread: Thread() {
        override fun run(){
            while(gameStatus){
                circleX+=dx
                circleY+=dy
                when{
                    circleX<radius -> {circleX = radius
                        dx *= -1}
                    circleX>width - radius -> {circleX = width - radius
                        dx *= -1}
                    circleY<height/20f + 20f + radius -> {circleY = height/20f + 20f + radius
                        dy *= -1
                        score+=2}
                    circleY >= height - radius - playerHeight -> {if(circleX in playerX..playerX + playerWidth){
                        circleY = height - radius - playerHeight
                        dy *= -1
                    } else{
                        gameStatus = false
                        val intent = Intent(this@GameView.context, ThirdActivity::class.java)
                        intent.putExtra("flag2", score)
                        this@GameView.context.startActivity(intent)
                    } }
                }
                postInvalidate()
                sleep(10)
            }
        }
    }
}