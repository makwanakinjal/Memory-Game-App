package com.example.memorygameapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.OneShotPreDrawListener.add
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() ,GameFragment.GameFragmentListener{

    var gridSize = 4

  private  var thisSecondTap = false
  private  lateinit var tile1 : Tile
    private  lateinit var tile2 : Tile

   private var gameActive = true

   private val foundTiles : ArrayList<Tile> = ArrayList()

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
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        restartGame()
        val restartButton = findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener {
            restartGame()
        }
    }
}