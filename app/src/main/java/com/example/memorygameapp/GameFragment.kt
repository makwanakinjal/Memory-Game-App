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
import android.widget.GridView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GameFragment(var gridSize: Int) : Fragment() {
    interface GameFragmentListener {

        fun makeTiles () : ArrayList<Tile>
        fun tileTapped (tile: Tile,index:Int)
    }
    private lateinit var scoreManager: ScoreManager
    private lateinit var recyclerView : RecyclerView
    private lateinit var caller: GameFragmentListener


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is GameFragmentListener){
            caller = context
            scoreManager = ScoreManager(context)
        }
    }
    override fun onCreateView(
                     inflater: LayoutInflater,
                     container: ViewGroup?,
                     savedInstanceState: Bundle? ): View? {

        val gridSize = arguments?.getInt("gridSize")?: 4

        val frag:View = inflater.inflate(R.layout.fragment_game,
                        container, false)


        val context = activity as Context
         recyclerView = frag.findViewById<RecyclerView>(R.id.gamerv)
        recyclerView.layoutManager = GridLayoutManager(context,gridSize)

        val tiles = caller.makeTiles()
        recyclerView.adapter = GameRecyclerAdapter(tiles)


        return frag
    }

    fun shuffleTiles(){
        val adapter = recyclerView.adapter as? GameFragment.GameRecyclerAdapter
        adapter?.inputData?.shuffle()
        adapter?.notifyDataSetChanged()
    }
    companion object {
        fun newInstance(gridSize:Int): GameFragment{

            val fragment = GameFragment(gridSize)
            val args = Bundle()
            args.putInt("gridSize",gridSize)
            fragment.arguments = args
            return fragment

        }
    }

    fun resetTiles() {
        shuffleTiles()
        val adapter = recyclerView.adapter as? GameRecyclerAdapter
        adapter?.resetTiles()
    }

  internal inner class GameRecyclerAdapter(val inputData: ArrayList<Tile>):
          RecyclerView.Adapter<GameRecyclerAdapter.RecyclerViewHolder> ()
  {
      fun resetTiles(){
          for(tile in inputData){
              tile.tileStatus = Status.UNKNOWN
              tile.updateTile()
          }
      }
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

