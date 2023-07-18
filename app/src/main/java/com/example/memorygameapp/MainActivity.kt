package com.example.memorygameapp


import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

enum class Difficulty{
    EASY , INTERMEDIATE , DIFFICULT
}
class MainActivity : AppCompatActivity() ,GameFragment.GameFragmentListener{
    var gridSize:Int =0

    private  var thisSecondTap = false
    private  lateinit var tile1 : Tile
    private  lateinit var tile2 : Tile
    private lateinit var winAnimation: AnimatorSet

    private var gameActive = true

    private val foundTiles : ArrayList<Tile> = ArrayList()

    private fun handleDifficultyLevel(difficulty: Difficulty){
        gridSize = when(difficulty){
            Difficulty.EASY -> 4
            Difficulty.INTERMEDIATE -> 6
            Difficulty.DIFFICULT -> 8
        }
       restartGame()
    }

    fun showGridSizeDialog(){

        val difficultyLevels = arrayOf("Easy", "Intermediate", "Difficult")

        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle("Select Difficulty Level")
            .setSingleChoiceItems(difficultyLevels,-1){
                dialog , which ->
                val difficulty = when(which){
                    0 -> Difficulty.EASY
                    1 -> Difficulty.INTERMEDIATE
                    2 -> Difficulty.DIFFICULT
                    else -> Difficulty.EASY
                }
                dialog.dismiss()
                handleDifficultyLevel(difficulty)
            }
            .setCancelable(false)

        val dialog = dialogBuilder.create()
        dialog.show()

    }


   override fun makeTiles(): ArrayList<Tile>{
        val tilesArray : ArrayList<Tile> = ArrayList()

       val centerIndex = gridSize * gridSize /2
       val isOddGridSize = gridSize % 2 == 1

       for (i in 1..gridSize * gridSize ){

           var num:Int
           if(isOddGridSize && i== centerIndex + 1){
               num = -1
           }
           else{
               num = i
               if (num> gridSize * gridSize /2){
                   num-= gridSize * gridSize/2
               }
           }

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
   private fun compare(){

        if(tile1.value == tile2.value){
            tile1.tileStatus = Status.FOUND
            tile2.tileStatus = Status.FOUND

            tile1.updateTile()
            tile2.updateTile()

            foundTiles.add(tile1)
            foundTiles.add(tile2)

            if(foundTiles.size == gridSize * gridSize){
                handleGameWin()
            }else{
                gameActive = true
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

    private fun handleGameWin(){
       stopTimer()
        winSoundPlayer.start()

       scoreManager.setTimer(elapsedTime.toInt() / 1000)
        scoreManager.setCurrentScore(foundTiles.size)

        if(foundTiles.size  >  scoreManager.getHighScore()){
            scoreManager.setHighScore(foundTiles.size)
        }



        val intent = Intent(this,CelebrationActivity::class.java)
        startActivity(intent)


    }

    private lateinit var timerTextView: TextView
    private var startTime: Long =0
    private var elapsedTime: Long =0
    private lateinit var timerHandler: Handler
    private lateinit var timerRunnable: Runnable

private lateinit var winSoundPlayer:MediaPlayer

   lateinit var scoreManager: ScoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showGridSizeDialog()

        timerTextView = findViewById(R.id.timerTextView)
        timerHandler = Handler()
        timerRunnable = Runnable { updateTimer() }


        winSoundPlayer =  MediaPlayer.create(this,R.raw.game_win_sound)

       scoreManager = ScoreManager(this)

    }
fun restartGame(){
        stopTimer()
        gameActive = true
        thisSecondTap = false
        foundTiles.clear()

        scoreManager.resetScores()

     // Remove the existing gamefragment if it exists
     val existingFragment = supportFragmentManager.findFragmentByTag("game")
     if(existingFragment != null){
         supportFragmentManager.beginTransaction()
             .remove(existingFragment)
             .commit()
     }

     // create a new GameFragment and start the game
     val gameFragment = GameFragment.newInstance(gridSize)
     supportFragmentManager.beginTransaction()
         .add(R.id.gameLayout,gameFragment,"game")
         .commit()

        startTimer()

    }


 fun startTimer(){

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
               // finish()
                finishAffinity()
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

