package com.matttske.gamelist.ui.gameDetails

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
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
                disableLoadingScreen()
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
        if (fb.getCachedLists().isEmpty()){
            Log.d("Game List", "Cached Firebase Lists Are Empty.")
            fb.getAllLists(object: Firebase.firebaseListsCachedCallback{
                override fun onSuccess() {
                    refreshGameStatus(id)
                }
            })
        }
        else{
            Log.d("Game List", "List was added using cached firebase list.")
            refreshGameStatus(id)
        }
    }

    private fun refreshGameStatus(id: Int){
        var gameFound = false
        fb.getCachedLists().forEach {
            if (it.second.contains(id.toLong())){
                gameFound = true
                gameStatus = it.first
            }
        }
        if (gameFound){
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
    }

    private fun disableLoadingScreen(){
        crossfade()
        animateViewUp()
    }

    private fun setViews(){
            gameTitle = findViewById(R.id.game_title)
            gameTitle.text = currentGame.name

            val gameDesc = findViewById<TextView>(R.id.game_description)
            gameDesc.text = Html.fromHtml(currentGame.description)

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
            }
        }
    }

    private fun addGameToList(listName: String){
        val idList: ArrayList<Int> = arrayListOf()
        fb.getCachedLists().forEach {
            if (it.first == listName){
                it.second.forEach {
                    idList.add(it.toInt())
                }
            }
        }
        idList.add(currentGame.id)
        fb.updateDocumentInGames(listName, idList, object: Firebase.firebaseListsCachedCallback{
            override fun onSuccess() {
                fb.getAllLists(object: Firebase.firebaseListsCachedCallback{
                    override fun onSuccess() {
                        refreshGameStatus(currentGame.id)
                    }
                })
                Toast.makeText(applicationContext, "${currentGame.name} was added to $listName!", Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun animateViewUp(){
        val mainView = findViewById<ScrollView>(R.id.main_content)
        ObjectAnimator.ofFloat(mainView, "translationY", 70f).apply {
            duration = resources.getInteger(android.R.integer.config_longAnimTime).toLong()
            start()
        }
    }

    private fun crossfade() {
        val shortAnimationDuration: Int = resources.getInteger(android.R.integer.config_longAnimTime)
        val mainView = findViewById<ScrollView>(R.id.main_content)
        mainView.apply {
            // Set the content view to 0% opacity but visible, so that it is visible
            // (but fully transparent) during the animation.
            alpha = 0f
            visibility = View.VISIBLE

            // Animate the content view to 100% opacity, and clear any animation
            // listener set on the view.
            animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration.toLong())
                .setListener(null)
        }
        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        val loadingScreen = findViewById<RelativeLayout>(R.id.loading_screen)
        loadingScreen.animate()
            .alpha(0f)
            .setDuration(shortAnimationDuration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    loadingScreen.visibility = View.GONE
                }
            })
    }
}