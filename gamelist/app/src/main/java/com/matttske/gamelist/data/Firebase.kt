package com.matttske.gamelist.data

import android.util.Log
import androidx.core.util.lruCache
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList


class Firebase {
    companion object FirebaseCache{

        private val db = FirebaseFirestore.getInstance()
        private val gamePath = "Users/${User.id()}/games"

        private var allLists: ArrayList<Pair<String, List<*>>> = arrayListOf()

        private fun getDocumentInGames(documentName: String, callback: firebaseCallback) {
            val idList = ArrayList<Int>()
            db.collection(gamePath)
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

        fun updateDocumentInGames(documentName: String, updatedIdList: List<Int>, callback: firebaseListsCachedCallback) {

            val newList = hashMapOf(
                "game_ids" to updatedIdList
            )

            db.collection(gamePath)
                .document(documentName)
                .set(newList)
                .addOnSuccessListener {
                    Log.d("Firestore", "DocumentSnapshot successfully written!")
                    callback.onSuccess()
                }
                .addOnFailureListener { e -> Log.w("Firestore", "Error writing document", e) }
        }

        fun deleteDocumentInGames(documentName: String, callback: writeCallback) {
            getCachedLists().forEach {
                if (it.first == documentName){
                    val index = getCachedLists().indexOf(it)
                    allLists.removeAt(index)
                }
            }
            db.collection(gamePath)
                .document(documentName)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "Document $documentName successfully deleted!")
                    callback.onSuccess()
                }
        }


        fun deleteGameFromDocuments(gameId: Int, callback: firebaseListsCachedCallback){
            val newListInt = arrayListOf<Int>()
            getCachedLists().forEach {
                if (it.second.contains(gameId.toLong())){
                    val index = it.second.indexOf(gameId.toLong())
                    val newList = it.second.toMutableList()
                    newList.forEach {
                        newListInt.add(it.toInt())
                    }
                    newListInt.removeAt(index)
                    updateDocumentInGames(it.first, newListInt, callback)
                }
            }
        }

        fun addGameToDocument(documentName: String, gameId: Int, callback: firebaseListsCachedCallback){
            val newListInt = arrayListOf<Int>()
            getCachedLists().forEach {
                if (it.first == documentName){
                    val newList = it.second.toMutableList()
                    newList.forEach {
                        newListInt.add(it.toInt())
                    }
                    newListInt.add(gameId)
                    updateDocumentInGames(it.first, newListInt, callback)
                }
            }
        }

        fun addNewGameList(
            listName: String,
            callback: writeCallback,
            initGames: List<Int> = ArrayList()
        ) {
            allLists.add(Pair(listName, initGames))
            val newList = hashMapOf(
                "game_ids" to initGames
            )

            db.collection(gamePath)
                .document(listName.toLowerCase(Locale.ROOT))
                .set(newList)
                .addOnSuccessListener {
                    Log.d("Firestore", "Game List with name $listName successfully written!")
                    callback.onSuccess()
                }
        }

        fun getAllListNames(callback: firebaseListNameCallback) {
            db.collection(gamePath)
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

        fun getAllLists(callback: firebaseListsCachedCallback) {
            db.collection(gamePath)
                .get()
                .addOnSuccessListener { result ->
                    allLists.clear()
                    for (document in result) {
                        if (document.get("game_ids") != null){
                            if (document.id != "favourites"){
                                val gameIds = document.get("game_ids") as List<Long>
                                allLists.add(Pair(document.id, gameIds))
                                callback.onSuccess()
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents.", exception)
                }
        }

        @Suppress("UNCHECKED_CAST")
        fun getCachedLists(): List<Pair<String, List<Long>>> {
            return allLists as List<Pair<String, List<Long>>>
        }

        fun searchGameInDocuments(callback: firebaseSearchListCallback, id: Int) {
            db.collection(gamePath)
                .whereArrayContains("game_ids", id)
                .get()
                .addOnSuccessListener { result ->
                    var listName = ""
                    for (document in result) {
                        listName = document.id
                    }
                    callback.onSuccess(listName)
                }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents.", exception)
                }
        }

    }
        interface firebaseCallback {
            fun onSuccess(idList: List<Int>)
        }

        interface firebaseListNameCallback {
            fun onSuccess(listNames: List<Pair<String, Int>>)
        }

        interface firebaseSearchListCallback {
            fun onSuccess(listName: String)
        }

        interface firebaseListsCachedCallback {
            fun onSuccess()
        }

        interface writeCallback {
            fun onSuccess()
        }
}
