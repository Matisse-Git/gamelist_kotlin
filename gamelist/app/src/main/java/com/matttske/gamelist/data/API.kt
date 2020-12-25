package com.matttske.gamelist.data

import android.text.AlteredCharSequence
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class API {

    val service = Retrofit.Builder()
        .baseUrl("https://api.rawg.io/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(GameService::class.java)

    fun getAllGames(returnCallBack: ReturnValueCallBack, page: Int){
        /* Calls the endpoint set on getUsers (/api) from UserService using enqueue method
        * that creates a new worker thread to make the HTTP call */
        service.getGames(page, 5).enqueue(object : Callback<GameResp> {
            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<GameResp>, t: Throwable) {
                Log.d("Called 'getAllGames'", "An error happened!")
                t.printStackTrace()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<GameResp>, response: Response<GameResp>) {
                Log.d("Called 'getAllGames'", "Call successful at page $page")
                returnCallBack.onSuccess(response.body()!!.results)
            }
        })
    }

    fun searchGames(returnCallBack: ReturnValueCallBack, query: String){
        service.searchGames(query).enqueue(object: Callback<GameResp> {
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
}

interface ReturnValueCallBack {
    fun onSuccess(value: List<Game>)
}

/* Kotlin data/model classes that map the JSON response, we could also add Moshi
 * annotations to help the compiler with the mappings on a production app */
data class GameResp(val results: List<Game>)

/* Retrofit service that maps the different endpoints on the API, you'd create one
 * method per endpoint, and use the @Path, @Query and other annotations to customize
 * these at runtime */
interface GameService {
    @GET("games")
    fun getGames(
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): Call<GameResp>

    @GET("games")
    fun searchGames(
        @Query("search") query: String
    ): Call<GameResp>
}

