package com.matttske.gamelist.data

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

class API {

    val service = Retrofit.Builder()
        .baseUrl("https://api.rawg.io/api/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(GameService::class.java)

    fun getAllGames(returnCallBack: ReturnValueCallBack){
        /* Calls the endpoint set on getUsers (/api) from UserService using enqueue method
        * that creates a new worker thread to make the HTTP call */
        service.getGames().enqueue(object : Callback<GameResp> {
            /* The HTTP call failed. This method is run on the main thread */
            override fun onFailure(call: Call<GameResp>, t: Throwable) {
                Log.d("Called 'getAllGames'", "An error happened!")
                t.printStackTrace()
            }

            /* The HTTP call was successful, we should still check status code and response body
             * on a production app. This method is run on the main thread */
            override fun onResponse(call: Call<GameResp>, response: Response<GameResp>) {
                Log.d("Called 'getAllGames'", "Call successful")
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
    @GET("games?page_size=20")
    fun getGames(): Call<GameResp>
}

