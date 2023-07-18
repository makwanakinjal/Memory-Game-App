package com.example.memorygameapp

import android.content.Context
import android.content.SharedPreferences


class ScoreManager(private val context: Context) {

    companion object {
        private const val KEY_HIGH_SCORE = "high_score"
        private const val KEY_TIMER = "timer"
        private const val KEY_CURRENT_SCORE = "current_score"
    }

private val sharedPreferences: SharedPreferences =
    context.getSharedPreferences("MyMemoryGameApp",Context.MODE_PRIVATE)

fun resetGame(){
    return sharedPreferences.edit().clear().apply()
}
    fun getHighScore():Int{
        return sharedPreferences.getInt(KEY_HIGH_SCORE,0)
    }

    fun setHighScore(score: Int) {
        val highScore = getHighScore()
        if(score > highScore){
            sharedPreferences.edit().putInt(KEY_HIGH_SCORE, score).apply()
        }
    }

    fun getTimer(): String {
        val seconds = sharedPreferences.getInt(KEY_TIMER, 0)

        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        return String.format("%02d:%02d", minutes, remainingSeconds)

    }
     fun setTimer(timer: Int) {
        sharedPreferences.edit().putInt(KEY_TIMER, timer).apply()
    }

    fun getCurrentScore(): Int {
        return sharedPreferences.getInt(KEY_CURRENT_SCORE, 0)
    }

    fun setCurrentScore(score: Int) {
        val currentScore = getCurrentScore()
        if(score > currentScore){
            sharedPreferences.edit().putInt(KEY_CURRENT_SCORE, score).apply()

        }
    }
    fun resetScores(){
        setTimer(0)
        setCurrentScore(0)

    }

}