package com.matttske.gamelist.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.matttske.gamelist.MainActivity
import com.matttske.gamelist.R
import com.matttske.gamelist.data.API
import com.matttske.gamelist.data.Game
import com.matttske.gamelist.data.GameRecycleAdapter
import com.matttske.gamelist.data.ReturnValueCallBack
import com.matttske.gamelist.ui.gameDetails.GameDetailed
import com.matttske.gamelist.ui.searching.search_game
import kotlin.random.Random

class HomeFragment : Fragment(), GameRecycleAdapter.OnItemCLickListener {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingCircle: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var newPageLoadingCircle: ProgressBar
    private lateinit var searchButton: MaterialButton

    private lateinit var gameList: ArrayList<Game>
    private lateinit var adapter: GameRecycleAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private var currentPage = 0

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
        gameList = ArrayList()
        adapter = GameRecycleAdapter(gameList, this@HomeFragment)
        adapter.addContext(requireContext())
        recyclerView.adapter = adapter

        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        activity?.setTheme(R.style.Theme_Gamelist)

        getGameList()

        return root
    }

    private fun findViews(root: View) {
        recyclerView = root.findViewById(R.id.recycler_view)
        loadingCircle = root.findViewById(R.id.loading_circle)
        loadingText = root.findViewById(R.id.loading_text)

        newPageLoadingCircle = root.findViewById(R.id.new_page_loading_circle)
        newPageLoadingCircle.visibility = View.GONE

        searchButton = root.findViewById(R.id.search_card)

        searchButton.setOnClickListener {
            val intent = Intent(activity, search_game::class.java)
            startActivity(intent)
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(layoutManager.findLastVisibleItemPosition() == layoutManager.itemCount-1){
                    newPageLoadingCircle.visibility = View.VISIBLE
                    getGameList()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
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
                gameList.addAll(value)
                loadingCircle.visibility = View.GONE
                loadingText.visibility = View.GONE
                newPageLoadingCircle.visibility = View.GONE
                adapter.notifyDataSetChanged()
            }
        }

        currentPage++
        API().getAllGames((callbackObj), currentPage)
    }

    override fun onItemClick(game: Game, gameTitle: TextView) {
        val intent = Intent(context, GameDetailed::class.java)
        intent.putExtra("Game", game)

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(parentFragment?.activity as MainActivity, gameTitle, ViewCompat.getTransitionName(gameTitle)!!)
        startActivity(intent, options.toBundle())
    }
}