package com.example.memorygameapp

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() ,GameFragment.GameFragmentListener{


   override fun makeTiles(): ArrayList<TextView>{
        val tilesArray : ArrayList<TextView> = ArrayList()
        for (i in 1..16){
            val newTile = TextView(this)
            newTile.text = "HI"
            newTile.setTextColor(Color.BLACK)
            newTile.setBackgroundColor(Color.YELLOW)

            tilesArray.add(newTile)
        }
        return tilesArray
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.gameLayout,GameFragment.newInstance(),"game").commit()
    }
}