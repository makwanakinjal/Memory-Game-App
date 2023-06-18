package com.example.memorygameapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
         val SPLASH_DELAY = 2000L

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)

    }
}