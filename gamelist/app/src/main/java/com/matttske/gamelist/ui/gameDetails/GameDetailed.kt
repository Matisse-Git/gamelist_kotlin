package com.matttske.gamelist.ui.gameDetails

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.google.android.material.button.MaterialButton
import com.matttske.gamelist.R
import com.matttske.gamelist.data.*
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.json.JSONStringer
import kotlin.collections.ArrayList

private val fb = Firebase.FirebaseCache
private val api = API()

private var gameId: Int = 0
private lateinit var currentGame: Game //= Game(0, "dummy", "dummy", "dummy", false, "dummy", "dummy", "dummy", "dummy", arrayOf(), Clip("",Clips(""),"",""), AddedByStatus(0,0,0,0,0,0))
private lateinit var currentMovies: List<Movie>
private lateinit var currentScreenshots: List<Screenshot>

private lateinit var gameStatus: String

private lateinit var gameTitle: TextView

class GameDetailed : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detailed)

        gameId = intent.getSerializableExtra("Game") as Int

        setDetailedGame()
        findListName(gameId)
    }

    private fun setDetailedGame() {
        val callback = object : SingleReturnValueCallBack {
            override fun onSuccess(value: Game) {
                currentGame = value
                setViews()
            }
        }
        val movieCallback = object: GameMovieValueCallBack{
            override fun onSuccess(value: List<Movie>) {
                if (value.isNotEmpty()){
                    currentMovies = value
                }
            }
        }
        val screenshotsCallBack = object: GameScreenshotsValueCallBack{
            override fun onSuccess(value: List<Screenshot>) {
                if (value.isNotEmpty()){
                    currentScreenshots = value
                }
            }
        }
        api.getGameById(callback, gameId)
        api.getMovieOfGame(movieCallback, gameId)
        api.getScreenshotsOfGame(screenshotsCallBack, gameId)
    }

    private fun findListName(id: Int){
        val fbcallback = object: Firebase.firebaseSearchListCallback{
            override fun onSuccess(listName: String) {
                if (listName != ""){
                    gameStatus = listName
                    val gameListButton = findViewById<MaterialButton>(R.id.list_button)
                    gameListButton.visibility = View.GONE
                    val addedGameListButton = findViewById<MaterialButton>(R.id.added_list_button)
                    addedGameListButton.text = gameStatus
                    addedGameListButton.setOnClickListener {
                        startFilterActivity(1)
                    }
                }
                else{
                    val gameListButton = findViewById<MaterialButton>(R.id.list_button)
                    gameListButton.setOnClickListener {
                        startFilterActivity(1)
                    }
                }
                disableLoadingScreen()
            }
        }
        fb.searchGameInDocuments(fbcallback, gameId)
    }

    private fun disableLoadingScreen(){
        val loadingScreen = findViewById<RelativeLayout>(R.id.loading_screen)
        loadingScreen.visibility = View.GONE
    }

    private fun setViews(){
            gameTitle = findViewById(R.id.game_title)
            gameTitle.text = currentGame.name

            val gameBackground = findViewById<ImageView>(R.id.game_background)
            Glide.with(this)
                    .load(currentGame.background_image_additional)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(bitmapTransform(RoundedCornersTransformation(25, 5)))
                    .into(gameBackground)

            val gameIcon = findViewById<ImageView>(R.id.game_icon)
            Glide.with(this)
                    .load(currentGame.background_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .circleCrop()
                    .into(gameIcon)
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