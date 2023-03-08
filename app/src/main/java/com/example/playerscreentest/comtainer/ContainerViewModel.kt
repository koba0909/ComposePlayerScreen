package com.example.playerscreentest.comtainer

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ContainerState(
    val isPortrait: Boolean,
    val chatUiType: ChatUiType,
)

enum class ChatUiType {
    NONE, RIGHT, BOTTOM_2, BOTTOM_4
}

@HiltViewModel
class ContainerViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ContainerState(true, ChatUiType.NONE))
    val state = _state.asStateFlow()

    fun onToggleRotation() {
        _state.update {
            it.copy(
                isPortrait = _state.value.isPortrait,
            )
        }
    }

    fun nextChatUiType() {
        _state.update {
            it.copy(
                chatUiType = when (_state.value.chatUiType) {
                    ChatUiType.RIGHT -> ChatUiType.BOTTOM_4
                    ChatUiType.BOTTOM_4 -> ChatUiType.BOTTOM_2
                    ChatUiType.BOTTOM_2 -> ChatUiType.NONE
                    ChatUiType.NONE -> ChatUiType.RIGHT
                }
            )
        }
    }
}
