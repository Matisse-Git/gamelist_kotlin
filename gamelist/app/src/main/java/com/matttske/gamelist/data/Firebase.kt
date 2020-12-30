package com.matttske.gamelist.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class Firebase {
    private val db = FirebaseFirestore.getInstance()

    fun getDocumentInGames(documentName: String, callback: firebaseCallback){
        val idList = ArrayList<Int>()
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
                        Log.d("FB List", idList.size.toString())
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
                .addOnFailureListener { e -> Log.w("Firestore", "Error writing document", e) }
    }

    fun getAllListNames(callback: firebaseListNameCallback){
        db.collection("Users/${User.id()}/games")
                .get()
                .addOnSuccessListener { result ->
                    val listNames = ArrayList<Pair<String, Int>>()
                    for (document in result) {
                        val listCount = document.get("game_ids") as List<*>
                        listNames.add(Pair(document.id, listCount.size))
                    }
                    callback.onSuccess(listNames)
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents.", exception)
                }
    }

    interface firebaseCallback{
        fun onSuccess(idList: List<Int>)
    }

    interface firebaseListNameCallback{
        fun onSuccess(listNames: List<Pair<String, Int>>)
    }
}