package com.example.memorygameapp

import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.OneShotPreDrawListener.add
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() ,GameFragment.GameFragmentListener{
    var gridSize:Int =0

    private  var thisSecondTap = false
    private  lateinit var tile1 : Tile
    private  lateinit var tile2 : Tile

    private var gameActive = true

    private val foundTiles : ArrayList<Tile> = ArrayList()

    fun playWinSound(){
        winSoundPlayer.start()
    }


    fun showGridSizeDialog(){


val gridSizeEditText = EditText(this)
        gridSizeEditText.hint = "Enter Grid Size"
        gridSizeEditText.inputType = InputType.TYPE_CLASS_NUMBER

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(gridSizeEditText)
            .setTitle("Grid Size")
            .setCancelable(false)
            .setPositiveButton("Confirm") {
                    dialog , _ ->
                val gridSizeText = gridSizeEditText.text.toString()
                val gridSize = gridSizeText.toIntOrNull()

                if(gridSize!= null && gridSize>= 2){
                    this.gridSize = gridSize
                    restartGame()
                    dialog.dismiss()
                }
                else{
                    Toast.makeText(this,"Invalid Grid Size!",Toast.LENGTH_LONG).show()
                }
            }

        val dialog = dialogBuilder.create()
        dialog.show()
       // gameStartSound()
    }

   override fun makeTiles(): ArrayList<Tile>{
        val tilesArray : ArrayList<Tile> = ArrayList()
        for (i in 1..gridSize * gridSize){

            var num:Int = i
            if(num> gridSize * gridSize /2)
                num-= gridSize * gridSize/2

            val newTile = Tile(this,num)

            newTile.updateTile()

            tilesArray.add(newTile)
        }

       tilesArray.shuffle()
        return tilesArray
    }

    override fun tileTapped(tile: Tile, index: Int){

        if(!gameActive || tile.tileStatus == Status.FOUND || tile.tileStatus == Status.FLIPPED)
            return

        tile.tileStatus = Status.FLIPPED
        tile.updateTile()

        if(!thisSecondTap) // 1st tap
        {
            tile1 = tile
            thisSecondTap = true
        } else{
            tile2 = tile
            thisSecondTap = false

            gameActive = false
            Handler().postDelayed({
                compare()
            },1000)


        }
    }

    fun compare(){

        if(tile1.value == tile2.value){
            tile1.tileStatus = Status.FOUND
            tile2.tileStatus = Status.FOUND

            tile1.updateTile()
            tile2.updateTile()

            foundTiles.add(tile1)
            foundTiles.add(tile2)

            if(foundTiles.size == gridSize * gridSize){
                stopTimer()
                playWinSound()
                // won the game
                Toast.makeText(this,"You Won the Game",Toast.LENGTH_LONG).show()
            }
        }
        else{
            tile1.tileStatus = Status.UNKNOWN
            tile2.tileStatus = Status.UNKNOWN

            tile1.updateTile()
            tile2.updateTile()
        }
        gameActive = true
    }

   private fun restartGame(){

        gameActive = true
        thisSecondTap = false
        foundTiles.clear()


        val frag:Fragment? = supportFragmentManager.findFragmentByTag("game")

        if(frag!= null){
            supportFragmentManager.beginTransaction()
                .remove(frag).commit()
        }
        supportFragmentManager
            .beginTransaction()
            .add(R.id.gameLayout,GameFragment.newInstance(gridSize),
                "game").commit()

       startTimer()
    }

    private lateinit var timerTextView: TextView
    private var startTime: Long =0
    private var elapsedTime: Long =0
    private lateinit var timerHandler: Handler
    private lateinit var timerRunnable: Runnable

private lateinit var winSoundPlayer:MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showGridSizeDialog()
       // restartGame()
        val restartButton = findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener {
            restartGame()
        }

        timerTextView = findViewById(R.id.timerTextView)
        timerHandler = Handler()
        timerRunnable = Runnable { updateTimer() }

      winSoundPlayer =  MediaPlayer.create(this,R.raw.game_win_sound)


    }


    private fun startTimer(){

        startTime = System.currentTimeMillis()
        timerHandler.postDelayed(timerRunnable,0)
    }
    private fun stopTimer(){

        timerHandler.removeCallbacks(timerRunnable)
    }
    private fun updateTimer(){
        elapsedTime = System.currentTimeMillis() - startTime
        val seconds = (elapsedTime / 1000).toInt()
        val minutes = seconds/60
        val secondsDisplay = seconds % 60
        timerTextView.text = String.format("%02d:%02d",minutes,secondsDisplay)
        timerHandler.postDelayed(timerRunnable,1000)
    }
    private fun showExitDialog(){
        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Exit Game")
            .setMessage("Are you sure , You want to exit the game?")
            .setPositiveButton("Exit"){
                dialog, _ ->
                finish()
            }
            .setNegativeButton("Cancel") {
                dialog,  _ ->
                dialog.dismiss()
            }
            .setCancelable(false)

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        showExitDialog()
    }


}