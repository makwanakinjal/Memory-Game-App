package com.example.memorygameapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.PixelCopy
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CelebrationActivity : AppCompatActivity() {
    private lateinit var scoreManager: ScoreManager
    private lateinit var tvTimer: TextView
    private lateinit var tvCurrentScore: TextView
    private lateinit var tvHighScore: TextView
    private lateinit var restartBtn : Button
    private lateinit var exitBtn : Button
    private lateinit var shareBtn : Button
    private lateinit var rootView : RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_celebration)

        scoreManager = ScoreManager(this)

        tvTimer = findViewById(R.id.tvTimer)
        tvCurrentScore = findViewById(R.id.tvCurrentScore)
        tvHighScore = findViewById(R.id.tvHighScore)
        restartBtn = findViewById(R.id.restartButton)
        exitBtn = findViewById(R.id.exitbtn)
        shareBtn = findViewById(R.id.sharingButton)
        rootView = findViewById(R.id.celebration)


        updateScoreViews()

        restartBtn.setOnClickListener {
                restart()
        }

        exitBtn.setOnClickListener {
            exit()
        }

        shareBtn.setOnClickListener {
            share()
        }

    }
    private fun restart(){

        scoreManager.resetScores()

        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun exit(){
        finishAffinity()
    }

    private fun share(){
        val pageBitmap = getBitmapFromView(rootView)
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_STREAM,saveBitmapToCache(pageBitmap))

        val shareIntent = Intent.createChooser(sharingIntent, "Share Score")

        // Check if there are any apps available for sharing
        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(shareIntent)
        } else {
            Toast.makeText(this, "No app found to share the score.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
    private fun saveBitmapToCache(bitmap: Bitmap): Uri?{
        val cachePath = File(cacheDir , "images")
        cachePath.mkdirs()

        try{
            val file = File(cachePath,"page_image.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
            outputStream.close()
            return FileProvider.getUriForFile(this,"$packageName.fileprovider",file)
        } catch (e: IOException){
            e.printStackTrace()
        }

        return null
    }

    private fun updateScoreViews(){

      val scoreTimer = scoreManager.getTimer().toString()
      val currentScore  = scoreManager.getCurrentScore().toString()
     val highScore  = scoreManager.getHighScore().toString()


        tvTimer.text = "Time taken by You: $scoreTimer"
        tvCurrentScore.text = "Current Score: $currentScore"
        tvHighScore.text = "High Score : $highScore"

    }
}