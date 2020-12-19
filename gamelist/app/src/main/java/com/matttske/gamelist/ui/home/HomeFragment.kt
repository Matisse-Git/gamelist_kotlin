package com.matttske.gamelist.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var gameList: List<Game>
    private lateinit var recyclerView: RecyclerView

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

        getGameList()
        recyclerView = root.findViewById(R.id.recycler_view)

        return root
    }

    private fun getGameList() {
        val callbackObj = object : ReturnValueCallBack {
            override fun onSuccess(value: List<Game>) {
                gameList = value
                recyclerView.adapter = GameRecycleAdapter(gameList)
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.setHasFixedSize(true)
            }
        }

        API().getAllGames((callbackObj))
    }
}