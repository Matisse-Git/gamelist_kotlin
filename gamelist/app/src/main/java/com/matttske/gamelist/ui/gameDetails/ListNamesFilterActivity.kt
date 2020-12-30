package com.matttske.gamelist.ui.gameDetails

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
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

        val addListButton = findViewById<Button>(R.id.add_list_button)
        addListButton.setOnClickListener {
            addNewList()
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
                fb.getAllListNames(fbNameListCallbackObj)
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
        sendDataBack(listName)
    }


}