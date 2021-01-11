package com.matttske.gamelist.ui.dashboard

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.matttske.gamelist.MainActivity
import com.matttske.gamelist.R
import com.matttske.gamelist.data.*
import com.matttske.gamelist.ui.searching.SearchBarInput
import com.matttske.gamelist.ui.gameDetails.GameDetailed
import com.matttske.gamelist.ui.gameDetails.ListNamesFilterActivity
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment : Fragment(), SearchBarInput, GameRecycleAdapter.OnItemCLickListener {

    private lateinit var dashboardViewModel: DashboardViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var saveButton: Button
    private lateinit var filterListNameButton: FloatingActionButton
    lateinit var searchInput: EditText

    private lateinit var gameList: ArrayList<Game>
    private val idList = ArrayList<Int>()
    private lateinit var adapter: GameRecycleAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private val fb = Firebase()
    private val api = API()
    val callbackObj = object : SingleReturnValueCallBack {
        override fun onSuccess(value: Game) {
            gameList.add(value)
            reorderList()
        }
    }
    private val fbCallbackObj = object : Firebase.firebaseCallback {
        override fun onSuccess(newIdList: List<Int>) {
            idList.clear()
            idList.addAll(newIdList)
            gameList.clear()
            Log.d("New List", newIdList.size.toString())
            Log.d("List", idList.size.toString())
            for (id in idList){
                api.getGameById(callbackObj, id)
            }
        }
    }
    private val touchHelper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,0){
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val sourcePos = viewHolder.adapterPosition
            val targetPos = target.adapterPosition
            Collections.swap(idList, sourcePos, targetPos)
            Collections.swap(gameList, sourcePos, targetPos)
            adapter.notifyItemMoved(sourcePos, targetPos)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            TODO("Not yet implemented")
        }
    })

    private var currentListName = "playing"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        findViews(root)

        fb.getDocumentInGames(currentListName, fbCallbackObj)

        return root
    }



    private fun reorderList() {
        val newGameList = ArrayList<Game>()

        for (id in idList){
            val index = gameList.indexOfFirst { it.id == id }
            if (index != -1)
                newGameList.add(gameList[index])
        }

        if (newGameList.size == idList.size){
            gameList.clear()
            gameList.addAll(newGameList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun updateFirestoreList(){
        Toast.makeText(activity?.applicationContext, "Your list has been saved!", Toast.LENGTH_SHORT).show()
        fb.updateDocumentInGames(currentListName, idList)
    }

    private fun findViews(root: View) {
        recyclerView = root.findViewById(R.id.recycler_view)
        saveButton = root.findViewById(R.id.save_button)
        saveButton.setOnClickListener{
            updateFirestoreList()
        }
        filterListNameButton = root.findViewById(R.id.filter_list_name_button)
        filterListNameButton.setOnClickListener{
            startFilterActivity(1)
        }
        //searchInput = root.findViewById(R.id.search_input)

        gameList = ArrayList()
        adapter = GameRecycleAdapter(gameList, this@DashboardFragment)
        adapter.addContext(requireContext())
        recyclerView.adapter = adapter

        layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun startFilterActivity(requestCode: Int) {
        val intent = Intent(activity, ListNamesFilterActivity::class.java)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                currentListName = data?.getStringExtra("result").toString()
                fb.getDocumentInGames(currentListName, fbCallbackObj)
            }
        }
    }


    override fun backPressed(view: View){}

    override fun clearInput(view: View){
        //searchInput.text.clear()
    }

    override fun onItemClick(id: Int, gameTitle: TextView) {
        val intent = Intent(context, GameDetailed::class.java)
        intent.putExtra("Game", id)

        startActivity(intent)
    }

}