package com.matttske.gamelist.ui.gameDetails

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.matttske.gamelist.R
import com.matttske.gamelist.data.Firebase
import com.matttske.gamelist.data.Game
import com.matttske.gamelist.data.ListNameFIlterAdapter
import com.matttske.gamelist.data.SingleReturnValueCallBack


class ListNamesFilterActivity : AppCompatActivity(), ListNameFIlterAdapter.OnItemCLickListener {

    private val fb = Firebase.FirebaseCache

    private var deleteMode = false
    private lateinit var deleteNotice: TextView

    private val defaultLists = arrayListOf<String>("playing", "completed", "paused", "dropped", "backlog", "wishlist")
    private val listNames = ArrayList<Pair<String, Int>>()
    private lateinit var listNameAdapter: ListNameFIlterAdapter
    private val fbNameListCallbackObj = object : Firebase.firebaseListNameCallback {
        override fun onSuccess(newListNames: List<Pair<String, Int>>) {
            Log.d("Firestore", newListNames.toString())
            listNames.clear()
            listNameAdapter.notifyDataSetChanged()
            listNames.addAll(newListNames)
            listNameAdapter.notifyItemRangeChanged(0, newListNames.size)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_names_filter)

        findViews()

        if (fb.getCachedLists().isEmpty()){
            Log.d("Game List", "Cached Firebase Lists Are Empty.")
            fb.getAllLists(object: Firebase.firebaseListsCachedCallback{
                override fun onSuccess() {
                    refreshListNames()
                }
            })
        }
        else{
            Log.d("Game List", "List was added using cached firebase list.")
            refreshListNames()
        }
    }

    private fun refreshListNames(){
        listNames.clear()
        fb.getCachedLists().forEach {
            listNames.add(Pair(it.first, it.second.size))
        }
        listNameAdapter.notifyDataSetChanged()
    }

    private fun findViews() {
        listNameAdapter = ListNameFIlterAdapter(listNames, this)
        listNameAdapter.addContext(this)
        val listNameRecyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        listNameRecyclerView.adapter = listNameAdapter
        listNameRecyclerView.layoutManager = LinearLayoutManager(this)
        listNameRecyclerView.setHasFixedSize(true)

        deleteNotice = findViewById(R.id.delete_notice_text)
        deleteNotice.visibility = View.GONE

        val addListButton = findViewById<Button>(R.id.add_list_button)
        addListButton.setOnClickListener {
            addNewList()
        }
        val deleteListButton = findViewById<Button>(R.id.delete_list_button)
        deleteListButton.setOnClickListener{
            deleteMode = !deleteMode
            if (deleteMode){
                deleteNotice.visibility = View.VISIBLE
            }
            else{
                deleteNotice.visibility = View.GONE
            }
        }
    }

    private fun sendDataBack(result: String){
        val returnIntent = Intent()
        returnIntent.putExtra("result", result)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun addNewList(){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_new_list, null)
        val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("New List")
        val  mAlertDialog = mBuilder.show()

        val callbackObj = object : Firebase.writeCallback {
            override fun onSuccess() {
                listNames.clear()
                fb.getCachedLists().forEach {
                    listNames.add(Pair(it.first, it.second.size))
                }
                listNameAdapter.notifyItemRangeChanged(0, listNames.size)
            }
        }
        mDialogView.findViewById<Button>(R.id.confirm_button).setOnClickListener {
            mAlertDialog.dismiss()
            val listName = mDialogView.findViewById<TextInputEditText>(R.id.name_input).text.toString() //set the input text in TextView mainInfoTv.setText("Name:"+ name +"\nEmail: "+ email +"\nPassword: "+ password)
            fb.addNewGameList(listName, callbackObj)
        }

        mDialogView.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            mAlertDialog.dismiss()
        }

    }



    override fun onItemClick(listName: String) {
        if (!deleteMode)
            sendDataBack(listName)
        else{
            if (defaultLists.contains(listName)){
                Toast.makeText(applicationContext, "$listName is a default list and cannot be deleted.", Toast.LENGTH_LONG).show()
            }
            else{
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Warning")
                alertDialogBuilder.setMessage("By deleting this list, you are also deleting all the games that are inside of it. Are you sure you want to continue?")
                alertDialogBuilder.setCancelable(true)
                alertDialogBuilder.setNegativeButton("Cancel") {
                    dialog, which -> dialog.dismiss()
                }
                alertDialogBuilder.setPositiveButton("Confirm") {
                    dialog, which ->
                        dialog.dismiss()
                        val callbackObj = object : Firebase.writeCallback {
                            override fun onSuccess() {
                                listNames.clear()
                                fb.getCachedLists().forEach {
                                    listNames.add(Pair(it.first, it.second.size))
                                }
                                listNameAdapter.notifyItemRangeChanged(0, listNames.size)
                            }
                        }
                        fb.deleteDocumentInGames(listName, callbackObj)
                        Toast.makeText(applicationContext, "$listName was successfully deleted!", Toast.LENGTH_SHORT).show()
                }

                alertDialogBuilder.show()
            }
        }
    }


}