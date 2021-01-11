package com.matttske.gamelist.data

import android.accessibilityservice.AccessibilityService
import android.text.AlteredCharSequence
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

class API {

    val key = "8a347c499cf4455a9881f383e1438070"
    val service = Retrofit.Builder()
        .baseUrl("https://api.rawg.io/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(GameService::class.java)

    fun getAllGames(returnCallBack: ReturnValueCallBack, page: Int){
        service.getGames(key, page, 5).enqueue(object : Callback<GameResp> {
            override fun onFailure(call: Call<GameResp>, t: Throwable) {
                Log.d("Called 'getAllGames'", "An error happened!")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<GameResp>, response: Response<GameResp>) {
                Log.d("Called 'getAllGames'", "Call successful at page $page")
                returnCallBack.onSuccess(response.body()!!.results)
            }
        })
    }

    fun searchGames(returnCallBack: ReturnValueCallBack, query: String){
        service.searchGames(key, query).enqueue(object: Callback<GameResp> {
            override fun onFailure(call: Call<GameResp>, t: Throwable) {
                Log.d("Called 'searchGames'", "An error happened!")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<GameResp>, response: Response<GameResp>) {
                Log.d("Called 'searchGames'", "Call successful for query '$query'")
                returnCallBack.onSuccess(response.body()!!.results)
            }
        })
    }

    fun getGameById(returnCallBack: SingleReturnValueCallBack, id: Int){
        service.getGameById(id, key).enqueue(object: Callback<Game> {
            override fun onFailure(call: Call<Game>, t: Throwable) {
                Log.d("Called 'getGameById'", "An error happened!")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<Game>, response: Response<Game>) {
                Log.d("Called 'getGameById'", "Call successful for game id '$id'")
                Log.d("Game Searched", response.body()!!.toString())
                returnCallBack.onSuccess(response.body()!!)
            }
        })
    }

    fun getMovieOfGame(returnCallBack: GameMovieValueCallBack, id: Int){
        service.getMovieOfGame(id, key).enqueue(object: Callback<GameMovieResp> {
            override fun onFailure(call: Call<GameMovieResp>, t: Throwable) {
                Log.d("Called 'getMovieOfGame'", "An error happened!")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<GameMovieResp>, response: Response<GameMovieResp>) {
                Log.d("Called 'getMovieOfGame'", "Call successful for game id '$id'")
                returnCallBack.onSuccess(response.body()!!.results)
            }
        })
    }

    fun getScreenshotsOfGame(returnCallBack: GameScreenshotsValueCallBack, id: Int){
        service.getScreenshotsOfGame(id, key).enqueue(object: Callback<GameScreenshotsResp> {
            override fun onFailure(call: Call<GameScreenshotsResp>, t: Throwable) {
                Log.d("Called 'getScreenshotsOfGame'", "An error happened!")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<GameScreenshotsResp>, response: Response<GameScreenshotsResp>) {
                Log.d("Called 'getScreenshotsOfGame'", "Call successful for game id '$id'")
                returnCallBack.onSuccess(response.body()!!.results)
            }
        })
    }
}

interface ReturnValueCallBack {
    fun onSuccess(value: List<Game>)
}

interface SingleReturnValueCallBack{
    fun onSuccess(value: Game)
}

interface GameMovieValueCallBack{
    fun onSuccess(value: List<Movie>)
}

interface GameScreenshotsValueCallBack{
    fun onSuccess(value: List<Screenshot>)
}

/* Kotlin data/model classes that map the JSON response, we could also add Moshi
 * annotations to help the compiler with the mappings on a production app */
data class GameResp(val results: List<Game>)
data class GameMovieResp(val results: List<Movie>)
data class GameScreenshotsResp(val results: List<Screenshot>)

/* Retrofit service that maps the different endpoints on the API, you'd create one
 * method per endpoint, and use the @Path, @Query and other annotations to customize
 * these at runtime */
interface GameService {
    @GET("games")
    fun getGames(
        @Query("key") key: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): Call<GameResp>

    @GET("games")
    fun searchGames(
        @Query("key") key: String,
        @Query("search") query: String
    ): Call<GameResp>

    @GET("games/{id}")
    fun getGameById(
        @Path("id") id: Int,
        @Query("key") key: String
    ): Call<Game>

    @GET("games/{id}/movies")
    fun getMovieOfGame(
            @Path("id") id: Int,
            @Query("key") key: String
    ): Call<GameMovieResp>

    @GET("games/{id}/screenshots")
    fun getScreenshotsOfGame(
            @Path("id") id: Int,
            @Query("key") key: String
    ): Call<GameScreenshotsResp>}

