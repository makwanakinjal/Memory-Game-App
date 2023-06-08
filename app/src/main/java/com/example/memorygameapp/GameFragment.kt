package com.example.memorygameapp

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameFragment(var gridSize: Int) : Fragment() {
    interface GameFragmentListener {

        fun makeTiles () : ArrayList<Tile>
        fun tileTapped (tile: Tile,index:Int)
    }

    private lateinit var caller: GameFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is GameFragmentListener){
            caller = context
        }
    }
    override fun onCreateView(
                     inflater: LayoutInflater,
                     container: ViewGroup?,
                     savedInstanceState: Bundle? ): View? {

        val frag:View = inflater.inflate(R.layout.fragment_game,
                        container, false)


        val context = activity as Context
        val recyclerView = frag.findViewById<RecyclerView>(R.id.gamerv)
        recyclerView.layoutManager = GridLayoutManager(context,gridSize)

        val tiles = caller.makeTiles()
        recyclerView.adapter = GameRecyclerAdapter(tiles)


        return frag
    }

    companion object {
        fun newInstance(gridSize:Int): GameFragment{
        return GameFragment(gridSize)
        }
    }

  internal inner class GameRecyclerAdapter(val inputData: ArrayList<Tile>):
          RecyclerView.Adapter<GameRecyclerAdapter.RecyclerViewHolder> ()

  {

      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

          val inflater = LayoutInflater.from(parent.context)
          val vh = RecyclerViewHolder(inflater,parent)
          return vh
      }

      override fun getItemCount(): Int = inputData.size

      override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

          val thisTile:Tile = inputData[position]

          val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT)

          thisTile.layoutParams = params
          params.setMargins(5,5,5,5)
          thisTile.gravity= Gravity.CENTER
          thisTile.textSize = 24F

          holder.tileParent.addView(thisTile)

          holder.tileParent.setOnClickListener {
              caller.tileTapped(thisTile,position)
          }
      }


      internal inner class RecyclerViewHolder (inflater: LayoutInflater,
                        parent : ViewGroup) :
              RecyclerView.ViewHolder(inflater.inflate(R.layout.card_list,parent,false)) {

                 val tileParent = itemView.findViewById<SquareFrameLayout>(R.id.tileParent)
      }

  }
}

