package com.matttske.gamelist.data

import android.annotation.SuppressLint
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matttske.gamelist.R

class ListNameFIlterAdapter(private val listNameFilterList: List<Pair<String, Int>>, private val listener: OnItemCLickListener): RecyclerView.Adapter<ListNameFIlterAdapter.ListNameItemViewholder>()  {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListNameItemViewholder {
        val listNameView = LayoutInflater.from(parent.context).inflate(R.layout.list_name_item, parent, false)

        return ListNameItemViewholder(listNameView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ListNameItemViewholder, position: Int) {
        val currentItem = listNameFilterList[position]

        holder.listNameView.text = currentItem.first
        holder.listCountView.text = "(${currentItem.second})"
    }

    override fun getItemCount() = listNameFilterList.size

    inner class ListNameItemViewholder(nameListView: View) : RecyclerView.ViewHolder(nameListView), View.OnClickListener{
        val listNameView: TextView = nameListView.findViewById(R.id.list_name)
        val listCountView: TextView = nameListView.findViewById(R.id.list_count)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            listener.onItemClick(listNameFilterList[adapterPosition].first)
        }
    }


    fun addContext(newContext: Context){
        context = newContext
    }

    interface OnItemCLickListener{
        fun onItemClick(listName: String)
    }
}