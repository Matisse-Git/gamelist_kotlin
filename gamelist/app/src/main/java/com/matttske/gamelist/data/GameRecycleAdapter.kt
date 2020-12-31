package com.matttske.gamelist.data

import android.content.Context
import android.graphics.Color
import android.graphics.Color.parseColor
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.matttske.gamelist.R
import de.hdodenhof.circleimageview.CircleImageView

class GameRecycleAdapter(private val gameList: List<Game>, private val listener: OnItemCLickListener) : RecyclerView.Adapter<GameRecycleAdapter.GameItemViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameItemViewHolder {
        val gameView = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)

        return GameItemViewHolder(gameView)
    }

    override fun onBindViewHolder(holder: GameItemViewHolder, position: Int) {
        val currentItem = gameList[position]

        //holder.cardView.setCardBackgroundColor(parseColor("#"+currentItem.dominant_color))

        holder.titleView.text = currentItem.name
        holder.releaseDateView.text = currentItem.released
        var genresString = ""
        for (g in currentItem.genres) {
            genresString += g.name
            if (g.name != currentItem.genres.last().name)
                genresString += ", "
        }
        holder.genresView.text = genresString

        if(currentItem.rating != "0.0")
            holder.ratingView.text = currentItem.rating
        else
            holder.ratingView.text = "?"

        if (currentItem.metacritic != null)
            holder.metacriticView.text = currentItem.metacritic
        else
            holder.metacriticView.text = "?"

        Glide.with(context).load(currentItem.background_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .circleCrop()
            .into(holder.backgroundView)
    }

    override fun getItemCount() = gameList.size


    inner class GameItemViewHolder(gameView: View) : RecyclerView.ViewHolder(gameView), View.OnClickListener{
        val titleView: TextView = gameView.findViewById(R.id.title_text)
        val releaseDateView: TextView = gameView.findViewById(R.id.release_text)
        val backgroundView: ImageView = gameView.findViewById(R.id.game_background)
        val ratingView: TextView = gameView.findViewById(R.id.rating_text)
        val metacriticView: TextView = gameView.findViewById(R.id.metacritic_text)
        val genresView: TextView = gameView.findViewById(R.id.game_genres)
        val cardView: CardView = gameView.findViewById(R.id.card_view)

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