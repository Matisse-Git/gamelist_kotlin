package com.matttske.gamelist.data

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matttske.gamelist.R

class GameRecycleAdapter(private val gameList: List<Game>) : RecyclerView.Adapter<GameRecycleAdapter.GameItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameItemViewHolder {
        val gameView = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)

        return GameItemViewHolder(gameView)
    }

    override fun onBindViewHolder(holder: GameItemViewHolder, position: Int) {
        val currentItem = gameList[position]

        holder.titleView.text = currentItem.name
        holder.subtitleView.text = currentItem.released
    }

    override fun getItemCount() = gameList.size


    class GameItemViewHolder(gameView: View) : RecyclerView.ViewHolder(gameView){
        val titleView: TextView = gameView.findViewById(R.id.title_text)
        val subtitleView: TextView = gameView.findViewById(R.id.subtitle_text)
    }
}