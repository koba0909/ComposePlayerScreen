package com.example.playerscreentest.container.domain.model

sealed class ChatUiType {
    object Right: ChatUiType()
    data class Bottom(val line: BottomChatLine): ChatUiType()
}

enum class BottomChatLine(line: Int) {
    ZERO(0), TWO(2), FOUR(4)
}