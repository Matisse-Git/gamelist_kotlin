package com.matttske.gamelist.ui.searching

import android.view.View

interface SearchBarInput {
    fun backPressed(view: View){}

    fun clearInput(view: View){}
}