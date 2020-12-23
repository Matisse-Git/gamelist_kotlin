package com.matttske.gamelist.data

import android.content.Context
import android.graphics.Color
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.matttske.gamelist.R

class GameRecycleAdapter(private val gameList: List<Game>, private val listener: OnItemCLickListener) : RecyclerView.Adapter<GameRecycleAdapter.GameItemViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameItemViewHolder {
        val gameView = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)

        return GameItemViewHolder(gameView)
    }

    override fun onBindViewHolder(holder: GameItemViewHolder, position: Int) {
        val currentItem = gameList[position]

        holder.titleView.text = currentItem.name
        holder.subtitleView.text = currentItem.released

        Glide.with(context).load(currentItem.background_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.backgroundView)
    }

    override fun getItemCount() = gameList.size


    inner class GameItemViewHolder(gameView: View) : RecyclerView.ViewHolder(gameView), View.OnClickListener{
        val titleView: TextView = gameView.findViewById(R.id.title_text)
        val subtitleView: TextView = gameView.findViewById(R.id.subtitle_text)
        val backgroundView: ImageView = gameView.findViewById(R.id.game_background)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            if (adapterPosition != RecyclerView.NO_POSITION){
                listener.onItemClick(gameList[adapterPosition], titleView)
            }
        }
    }

    fun addContext(newContext: Context){
        context = newContext
    }

    interface OnItemCLickListener{
        fun onItemClick(game: Game, gameTitle: TextView)
    }
}