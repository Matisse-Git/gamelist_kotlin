package com.matttske.gamelist.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class Firebase {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var callback: firebaseCallback
    private val idList = ArrayList<Int>()

    fun setCallBack(newCallback: firebaseCallback){
        this.callback = newCallback
    }

    fun getDocumentInGames(documentName: String){
        db.collection("Users/${User.id()}/games")
                .document(documentName.toLowerCase(Locale.ROOT))
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        for (id in document.get("game_ids") as List<*>) {
                            Log.d("Firestore", id.toString())
                            val newId = id.toString()
                            idList.add(newId.toInt())
                        }
                        callback.onSuccess(idList)
                    }

                }
    }

    fun updateDocumentInGames(documentName: String, updatedIdList: List<Int>){

        val newList = hashMapOf(
                "game_ids" to updatedIdList
        )

        db.collection("Users/${User.id()}/games")
                .document(documentName)
                .set(newList)
                .addOnSuccessListener {
                    Log.d("Firestore", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e -> Log.w("Firestore", "Error writing document", e) }    }

    interface firebaseCallback{
        fun onSuccess(idList: List<Int>)
    }
}