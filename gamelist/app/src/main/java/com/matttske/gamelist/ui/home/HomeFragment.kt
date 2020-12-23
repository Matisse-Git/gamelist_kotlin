package com.matttske.gamelist.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matttske.gamelist.R
import com.matttske.gamelist.data.API
import com.matttske.gamelist.data.Game
import com.matttske.gamelist.data.GameRecycleAdapter
import com.matttske.gamelist.data.ReturnValueCallBack
import kotlin.random.Random

class HomeFragment : Fragment(), GameRecycleAdapter.OnItemCLickListener {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingCircle: ProgressBar
    private lateinit var loadingText: TextView

    private lateinit var gameList: ArrayList<Game>
    private lateinit var adapter: GameRecycleAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        /*val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        findViews(root)
        getGameList()

        return root
    }

    private fun findViews(root: View) {
        recyclerView = root.findViewById(R.id.recycler_view)
        loadingCircle = root.findViewById(R.id.loading_circle)
        loadingText = root.findViewById(R.id.loading_text)

        val insertButton: Button = root.findViewById(R.id.insert_button)
        insertButton.setOnClickListener(View.OnClickListener {
            insertItem(root)
        })
        val removeButton: Button = root.findViewById(R.id.remove_button)
        removeButton.setOnClickListener(View.OnClickListener {
            removeItem(root)
        })
    }

    private fun insertItem(view: View) {
        val index = Random.nextInt(8)

        val newGame = Game(2, "new", "new", "new", true, "new", "new", "new")

        gameList.add(index, newGame)
        adapter.notifyItemInserted(index)
    }

    private fun removeItem(view: View){
        val index = Random.nextInt(8)

        gameList.removeAt(index)
        adapter.notifyItemRemoved(index)
    }

    private fun getGameList() {
        val callbackObj = object : ReturnValueCallBack {
            override fun onSuccess(value: List<Game>) {
                gameList = ArrayList(value)
                adapter = GameRecycleAdapter(gameList, this@HomeFragment)
                recyclerView.adapter = adapter

                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.setHasFixedSize(true)
                loadingCircle.visibility = View.GONE
                loadingText.visibility = View.GONE
            }
        }

        API().getAllGames((callbackObj))
    }

    override fun onItemClick(game: Game) {
        Log.d("Game Clicked", game.name)
    }
}