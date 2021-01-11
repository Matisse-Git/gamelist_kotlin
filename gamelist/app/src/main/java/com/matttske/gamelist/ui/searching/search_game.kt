package com.matttske.gamelist.ui.searching

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matttske.gamelist.R
import com.matttske.gamelist.data.API
import com.matttske.gamelist.data.Game
import com.matttske.gamelist.data.GameRecycleAdapter
import com.matttske.gamelist.data.ReturnValueCallBack
import com.matttske.gamelist.ui.gameDetails.GameDetailed

class search_game : AppCompatActivity(), GameRecycleAdapter.OnItemCLickListener, SearchBarInput {

    private lateinit var gameList: ArrayList<Game>
    private lateinit var adapter: GameRecycleAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var placeholderText: TextView
    private lateinit var noItemsImage: ImageView
    private lateinit var noItemsText: TextView

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
        placeholderText = findViewById(R.id.placeholder_text)
        placeholderText.visibility = View.VISIBLE
        noItemsText = findViewById(R.id.no_items_text)
        noItemsText.visibility = View.VISIBLE
        noItemsImage = findViewById(R.id.no_items_image)
        noItemsImage.visibility = View.VISIBLE

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val callbackObj = object : ReturnValueCallBack {
                    override fun onSuccess(value: List<Game>) {
                        gameList.clear()
                        gameList.addAll(value)
                        adapter.notifyDataSetChanged()

                        if (gameList.isEmpty()){
                            noItemsText.visibility = View.VISIBLE
                            noItemsImage.visibility = View.VISIBLE
                        } else{
                            noItemsText.visibility = View.GONE
                            noItemsImage.visibility = View.GONE
                        }
                    }
                }

                if (s.toString() == ""){
                    placeholderText.visibility = View.VISIBLE
                    gameList.clear()
                }
                else{
                    placeholderText.visibility = View.GONE
                    API().searchGames(callbackObj, s!!.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        searchInput.addTextChangedListener(textWatcher)
    }

    override fun backPressed(view: View){
        super.onBackPressed()
    }

    override fun clearInput(view: View){
        searchInput.text.clear()
    }

    override fun onItemClick(id: Int, gameTitle: TextView) {
        val intent = Intent(this, GameDetailed::class.java)
        intent.putExtra("Game", id)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, gameTitle, ViewCompat.getTransitionName(gameTitle)!!)
        startActivity(intent)
    }
}