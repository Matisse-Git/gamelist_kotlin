package com.matttske.gamelist.ui.gameDetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matttske.gamelist.R
import com.matttske.gamelist.data.Firebase
import com.matttske.gamelist.data.ListNameFIlterAdapter


class ListNamesFilterActivity : AppCompatActivity(), ListNameFIlterAdapter.OnItemCLickListener {

    private val fb = Firebase()

    private val listNames = ArrayList<Pair<String, Int>>()
    private lateinit var listNameAdapter: ListNameFIlterAdapter
    private val fbNameListCallbackObj = object : Firebase.firebaseListNameCallback {
        override fun onSuccess(newListNames: List<Pair<String, Int>>) {
            Log.d("Firestore", newListNames.toString())
            listNames.clear()
            listNames.addAll(newListNames)
            listNameAdapter.notifyItemRangeInserted(0, newListNames.size)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_names_filter)

        findViews()

        fb.getAllListNames(fbNameListCallbackObj)
    }

    private fun findViews() {
        listNameAdapter = ListNameFIlterAdapter(listNames, this)
        listNameAdapter.addContext(this)
        val listNameRecyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        listNameRecyclerView.adapter = listNameAdapter
        listNameRecyclerView.layoutManager = LinearLayoutManager(this)
        listNameRecyclerView.setHasFixedSize(true)
    }

    private fun sendDataBack(result: String){
        val returnIntent = Intent()
        returnIntent.putExtra("result", result)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    override fun onItemClick(listName: String) {
        Log.d("Name", listName)
        sendDataBack(listName)
    }


}