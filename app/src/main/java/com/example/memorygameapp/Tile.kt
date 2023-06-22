package com.example.memorygameapp

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.TextView

enum class Status{
    UNKNOWN,FLIPPED,FOUND
}
data class Tile (var myContext: Context, var value:Int) : androidx.appcompat.widget.AppCompatTextView (myContext){

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        setMeasuredDimension(width,width)
    }

    var tileStatus: Status = Status.UNKNOWN
    fun updateTile(){


        val objectAnim_1 = ObjectAnimator.ofFloat(this,
            "scaleX",1f,0f)

        val objectAnim_2 = ObjectAnimator.ofFloat(this,
            "scaleX",0f,1f)

        objectAnim_1.duration = 150
        objectAnim_2.duration = 150

        objectAnim_1.interpolator = DecelerateInterpolator()
        objectAnim_2.interpolator = AccelerateInterpolator()

        objectAnim_1.addListener(object : AnimatorListenerAdapter ()
        {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)

                when(tileStatus){
                    Status.UNKNOWN -> {
                        this@Tile.text = "🫥"
                        this@Tile.setBackgroundColor(Color.rgb(74,218,203))
                    }
                    Status.FLIPPED -> {

                        this@Tile.text = this@Tile.value.toString()
                        this@Tile.setBackgroundColor(Color.rgb(218,74,206))
                    }
                    Status.FOUND -> {

                        this@Tile.text = "😍"
                        this@Tile.setBackgroundColor(Color.rgb(78,125,232))
                    }
                }

                objectAnim_2.start()
            }
        })

        objectAnim_1.start()

    }
}