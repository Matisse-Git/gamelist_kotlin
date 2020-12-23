package com.matttske.gamelist.ui.gameDetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.matttske.gamelist.R
import com.matttske.gamelist.data.Game

private lateinit var currentGame: Game

private lateinit var gameTitle: TextView

class GameDetailed : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detailed)

        currentGame = intent.getSerializableExtra("Game") as Game

        gameTitle = findViewById(R.id.game_title)
        gameTitle.text = currentGame.name
    }
}