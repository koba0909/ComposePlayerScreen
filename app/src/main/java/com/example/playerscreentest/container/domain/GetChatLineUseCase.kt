package com.example.playerscreentest.container.domain

import com.example.playerscreentest.container.data.ChatUiRepository
import com.example.playerscreentest.container.domain.model.BottomChatLine
import javax.inject.Inject

class GetChatLineUseCase @Inject constructor(
   private val chatUiRepository: ChatUiRepository
){
    operator fun invoke() = chatUiRepository.getLine()
        .toBottomChatLine()
}

fun Int.toBottomChatLine(): BottomChatLine {
    return when(this) {
        0 -> BottomChatLine.ZERO
        2 -> BottomChatLine.TWO
        else -> BottomChatLine.FOUR
    }
}