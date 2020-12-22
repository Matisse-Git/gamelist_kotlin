package com.matttske.gamelist.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
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

    private lateinit var loadingCircle: ProgressBar
    private lateinit var loadingText: TextView

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

        recyclerView = root.findViewById(R.id.recycler_view)
        loadingCircle = root.findViewById(R.id.loading_circle)
        loadingText = root.findViewById(R.id.loading_text)
        getGameList()
        
        return root
    }

    public fun getGameList() {
        val callbackObj = object : ReturnValueCallBack {
            override fun onSuccess(value: List<Game>) {
                gameList = value
                recyclerView.adapter = GameRecycleAdapter(gameList)
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.setHasFixedSize(true)
                loadingCircle.visibility = View.GONE
                loadingText.visibility = View.GONE
            }
        }

        API().getAllGames((callbackObj))
    }
}