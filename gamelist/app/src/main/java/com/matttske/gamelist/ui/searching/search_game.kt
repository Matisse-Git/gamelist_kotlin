package com.matttske.gamelist.ui.searching

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matttske.gamelist.R
import com.matttske.gamelist.data.API
import com.matttske.gamelist.data.Game
import com.matttske.gamelist.data.GameRecycleAdapter
import com.matttske.gamelist.data.ReturnValueCallBack

class search_game : AppCompatActivity(), GameRecycleAdapter.OnItemCLickListener {

    private lateinit var gameList: ArrayList<Game>
    private lateinit var adapter: GameRecycleAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_game)


        getViews()
        searchInput.requestFocus()
        gameList = ArrayList()
        adapter = GameRecycleAdapter(gameList, this)
        adapter.addContext(this)
        recyclerView.adapter = adapter

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
    }

    private fun getViews(){
        searchInput = findViewById(R.id.search_input)
        recyclerView = findViewById(R.id.recycler_view)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val callbackObj = object : ReturnValueCallBack {
                    override fun onSuccess(value: List<Game>) {
                        gameList.clear()
                        gameList.addAll(value)
                        adapter.notifyDataSetChanged()
                    }
                }

                API().searchGames(callbackObj, s!!.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        searchInput.addTextChangedListener(textWatcher)
    }

    fun backPressed(view: View){
        super.onBackPressed()
    }

    override fun onItemClick(game: Game, gameTitle: TextView) {
        TODO("Not yet implemented")
    }
}