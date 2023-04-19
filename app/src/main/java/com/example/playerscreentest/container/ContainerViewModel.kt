package com.example.playerscreentest.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playerscreentest.container.domain.GetChatLineUseCase
import com.example.playerscreentest.container.domain.model.ChatUiType
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

sealed class ContainerEffect {
    object ToggleRotation: ContainerEffect()
}

@HiltViewModel
class ContainerViewModel @Inject constructor(
    private val chatUiRepository: ChatUiRepository,
    private val getChatLineUseCase: GetChatLineUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ContainerState(ChatUiType.Right))
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
                    is ChatUiType.Right -> ChatUiType.Bottom(
                        getChatLineUseCase()
                    )
                    is ChatUiType.Bottom -> ChatUiType.Right
                }
            )
        }
    }
}
