package com.example.pong

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.sqrt


class GameView(context: Context, attributes: AttributeSet) : View(context, attributes) {

    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    private var flag = 0
    private var elongatePlayer = 0
    private var mHandler = Handler()

    private val playerHeight = 40f
    private var playerWidth = 320f
    private val computerWidth = 320f
    private var playerX = 0f
    private var computerX = 0f

    private var touchX = 0f

    private var gameStatus = false
    var hardMode = false

    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    private val sound: SoundPool = SoundPool.Builder()
        .setMaxStreams(6)
        .setAudioAttributes(audioAttributes)
        .build()

    private val sound1 = sound.load(this.context, R.raw.ball_sound_edited, 1)
    private val sound2 = sound.load(this.context, R.raw.game_over_sound, 1)

    private val radius = 30f
    var circleX = ((screenWidth / 3)..(2 * screenWidth / 3)).random().toFloat()
    var circleY = if ((screenHeight / 20 + 90) < screenHeight / 2)
        ((screenHeight / 20 + 90)..screenHeight / 2).random().toFloat()
    else (0..screenHeight / 2).random().toFloat()
    private var dx = (3..10).random().toFloat()
    private var dy = sqrt((225 - dx * dx).toDouble()).toFloat()
    var playerScore = 0
    var computerScore = 0

    private var powerUpX = 0f
    private var powerUpY = 0f
    private var delayRunnable: Runnable? = null

    init {
        delayRunnable = Runnable {
            powerUpY = height / 3f
            flag = 1
            powerUpX = circleX
            mHandler.postDelayed(delayRunnable!!, 30000)
        }
    }

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

        playerX = (w / 2 - playerWidth / 2)
        computerX = (w / 2 - computerWidth / 2)
        paintText.textSize = h / 20f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBg)
        canvas.drawRect(
            playerX, height - (height / 20f + 20f + playerHeight),
            playerX + playerWidth, height - (height / 20f + 20f), paintFill
        )
        canvas.drawRect(
            computerX, height / 20f + 20f,
            computerX + computerWidth, height / 20f + 20f + playerHeight, paintFill
        )
        canvas.drawCircle(circleX, circleY, radius, paintFill)
        canvas.drawLine(
            0f, height / 20f + 20f,
            0f, height - (height / 20f + 20f), paintLine
        )
        canvas.drawLine(
            width.toFloat(), height / 20f + 20f,
            width.toFloat(), height - (height / 20f + 20f), paintLine
        )
        canvas.drawText(
            "SCORE: $computerScore",
            width / 2f - paintText.measureText("SCORE: $computerScore") / 2,
            height / 20f - 10,
            paintText
        )
        canvas.drawText(
            "SCORE: $playerScore", width / 2f - paintText.measureText("SCORE: $playerScore") / 2,
            height - 10f, paintText
        )

        if (flag == 1) canvas.drawRect(
            powerUpX,
            powerUpY,
            powerUpX + 30f,
            powerUpY + 30f,
            paintFill
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchX = event.x
            MotionEvent.ACTION_MOVE -> handleMove(event)
        }
        return true
    }

    private fun handleMove(event: MotionEvent) {
        playerX -= (touchX - event.x)
        touchX = event.x
        playerX = when {
            playerX < 0 -> 0f
            playerX > width - playerWidth -> width - playerWidth
            else -> playerX
        }
        invalidate()
    }

    fun startGame() {
        playerScore = 0
        computerScore = 0
        gameStatus = true
        GameThread().start()
    }

    fun gameOver() {
        mHandler.removeCallbacks(delayRunnable!!)
        gameStatus = false
        playSound(sound2)
        val intent = Intent(this.context, ThirdActivity::class.java)
        intent.putExtra("flag2", playerScore)
        this.context.startActivity(intent)
    }

    fun computersX() {
        computerX = when {
            (circleX - computerWidth / 2 <= 0f) -> 0f
            (circleX + computerWidth / 2 >= width.toFloat()) -> width - computerWidth
            else -> circleX - computerWidth / 2
        }
    }


    private var easyModeVar = (10..15).random()

    inner class GameThread : Thread() {
        override fun run() {
            mHandler.postDelayed(delayRunnable!!, 10000)
            while (gameStatus) {
                circleX += dx
                circleY += dy
                if (flag == 1) powerUpY += 5f
                if (hardMode) {
                    if (dx > 0) dx += 0.002f
                    else dx -= 0.002f
                    if (dy > 0) dy += 0.002f
                    else dy -= 0.002f

                    computersX()
                } else if (easyModeVar > 0) computersX()
                when {
                    circleX < radius -> {
                        circleX = radius
                        dx *= -1
                        playSound(sound1)
                    }
                    circleX > width - radius -> {
                        circleX = width - radius
                        dx *= -1
                        playSound(sound1)
                    }
                    circleY <= height / 20f + 20f + radius + playerHeight -> {
                        if (circleX in computerX..computerX + computerWidth) {
                            circleY = height / 20f + 20f + radius + playerHeight
                            dy *= -1
                            computerScore += 2
                            playSound(sound1)
                            if (easyModeVar > 0) easyModeVar -= 1
                            else easyModeVar = (5..10).random()
                        } else gameOver()
                    }
                    circleY >= height - (radius + playerHeight + height / 20f + 20f) -> {
                        if (circleX in playerX..playerX + playerWidth) {
                            circleY = height - (radius + playerHeight + height / 20f + 20f)
                            dy *= -1
                            playerScore += 2
                            playSound(sound1)
                            elongatePlayer--
                            if (elongatePlayer == 0) playerWidth -= 80f
                        } else gameOver()
                    }
                }
                if (powerUpY > height - (playerHeight + height / 20f + 20f) && powerUpY != 0f) {
                    if (powerUpX in playerX..playerX + playerWidth) {
                        playerWidth += 80f
                        elongatePlayer = 4
                    }
                    powerUpY = 0f
                    flag = 0
                }
                postInvalidate()
                sleep(10)
            }
        }
    }

    fun playSound(i: Int) {
        sound.play(i, 1f, 1f, 0, 0, 1f)
    }
}