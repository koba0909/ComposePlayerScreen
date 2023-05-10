package com.example.playerscreentest.container.data

import javax.inject.Inject

class ChatUiRepository @Inject constructor() {
    private var currentLine = 4

    fun setLine(line: Int) {
        currentLine = line
    }

    fun getLine() = currentLine
}