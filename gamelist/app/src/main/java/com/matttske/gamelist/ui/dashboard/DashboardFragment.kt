package com.matttske.gamelist.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.primitives.UnsignedBytes.toInt
import com.google.firebase.firestore.FirebaseFirestore
import com.matttske.gamelist.R
import com.matttske.gamelist.data.*
import com.matttske.gamelist.ui.SearchBarInput
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment : Fragment(), SearchBarInput, GameRecycleAdapter.OnItemCLickListener {

    private lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var recyclerView: RecyclerView
    lateinit var searchInput: EditText

    private lateinit var gameList: ArrayList<Game>
    private lateinit var adapter: GameRecycleAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val touchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val sourcePos = viewHolder.adapterPosition
            val targetPos = target.adapterPosition
            Collections.swap(gameList, sourcePos, targetPos)
            adapter.notifyItemMoved(sourcePos, targetPos)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }
    })

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        findViews(root)
        gameList = ArrayList()
        adapter = GameRecycleAdapter(gameList, this@DashboardFragment)
        adapter.addContext(requireContext())
        recyclerView.adapter = adapter

        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        touchHelper.attachToRecyclerView(recyclerView)

        getGameIds()

        return root
    }

    private fun getGameIds() {
        val apiInstance = API()
        val callbackObj = object : SingleReturnValueCallBack {
            override fun onSuccess(value: Game) {
                gameList.add(value)
                adapter.notifyDataSetChanged()
            }
        }

        db.collection("Users/${User.id()}/games")
            .document("completed")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    for (id in document.get("game_ids") as ArrayList<*>) {
                        Log.d("Firestore", id.toString())
                        val newId = id.toString()
                        apiInstance.getGameById(callbackObj, newId.toInt())
                    }
                }
            }
    }

    private fun findViews(root: View) {
        recyclerView = root.findViewById(R.id.recycler_view)
        //searchInput = root.findViewById(R.id.search_input)
    }

    override fun backPressed(view: View){}

    override fun clearInput(view: View){
        //searchInput.text.clear()
    }

    override fun onItemClick(game: Game, gameTitle: TextView) {
        TODO("Not yet implemented")
    }


}