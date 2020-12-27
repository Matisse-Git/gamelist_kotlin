package com.matttske.gamelist.ui

import android.view.View

interface SearchBarInput {
    fun backPressed(view: View){}

    fun clearInput(view: View){}
}