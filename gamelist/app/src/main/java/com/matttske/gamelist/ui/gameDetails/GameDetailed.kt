package com.matttske.gamelist.ui.gameDetails

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.matttske.gamelist.R
import com.matttske.gamelist.data.Firebase
import com.matttske.gamelist.data.Game
import com.matttske.gamelist.data.SingleReturnValueCallBack
import java.util.*
import kotlin.collections.ArrayList

private val fb = Firebase()
private lateinit var currentGame: Game

private lateinit var gameTitle: TextView

class GameDetailed : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detailed)

        currentGame = intent.getSerializableExtra("Game") as Game

        setViews()
    }

    private fun setViews(){
        gameTitle = findViewById(R.id.game_title)
        gameTitle.text = currentGame.name

        val addButton = findViewById<ImageButton>(R.id.add_button)
        addButton.setOnClickListener {
            startFilterActivity(1)
        }
    }

    private fun startFilterActivity(requestCode: Int) {
        val intent = Intent(this, ListNamesFilterActivity::class.java)
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                val listName = data?.getStringExtra("result").toString()
                addGameToList(listName)
                Toast.makeText(applicationContext, "${currentGame.name} was added to $listName!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addGameToList(listName: String){
        val idList = ArrayList<Int>()
        val fbCallbackObj = object : Firebase.firebaseCallback {
            override fun onSuccess(newIdList: List<Int>) {
                idList.addAll(newIdList)
                idList.add(currentGame.id)
                fb.updateDocumentInGames(listName, idList)
            }
        }

        fb.getDocumentInGames(listName, fbCallbackObj)
    }
}