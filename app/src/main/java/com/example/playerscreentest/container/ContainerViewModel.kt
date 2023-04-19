package com.example.playerscreentest.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContainerState(
    val chatUiType: ChatUiType,
)

enum class ChatUiType {
    NONE, RIGHT, BOTTOM_2, BOTTOM_4
}

sealed class ContainerEffect {
    object ToggleRotation: ContainerEffect()
}

@HiltViewModel
class ContainerViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(ContainerState(ChatUiType.NONE))
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ContainerEffect>()
    val effect = _effect.asSharedFlow()

    fun onToggleRotation() {
        viewModelScope.launch {
            _effect.emit(
                ContainerEffect.ToggleRotation
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
