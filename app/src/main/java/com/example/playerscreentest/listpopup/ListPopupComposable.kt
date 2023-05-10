package com.example.playerscreentest.listpopup

import android.util.Log
import androidx.compose.animation.core.AnimationState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.playerscreentest.container.ChattingScreen
import com.example.playerscreentest.container.ContainerState
import com.example.playerscreentest.container.PlayerScreen
import com.example.playerscreentest.container.domain.model.ChatUiType
import kotlin.math.roundToInt

@Composable
fun ListPopupComposable(
    modifier: Modifier = Modifier,
    topComposable: @Composable (Modifier) -> Unit,
    bottomComposable: @Composable (Modifier) -> Unit
) {
    var offsetY by remember { mutableStateOf(0f) }
    var topHeight by remember { mutableStateOf(0) }
    var columnWidth by remember { mutableStateOf(0) }
    var isMinimize by remember { mutableStateOf(false) }
    val topWidth by remember {
        derivedStateOf {
            if (isMinimize) {
                (columnWidth / 2).dp
            } else {
                columnWidth.dp
            }
        }
    }

    Log.d("hugh", "isMinimize:  $isMinimize")
    Log.d("hugh", "topWidth:  $topWidth")

    val topComposableModifier = Modifier
        .width(
            width = topWidth
        )
        .pointerInput(Unit) {
            detectDragGestures(
                onDragEnd = {
                    if (offsetY > topHeight / 3) {
                        // minimize
                        isMinimize = true
                    } else {
                        offsetY = 0f
                        isMinimize = false
                    }
                }
            ) { change, dragAmount ->
                change.consume()
                offsetY += dragAmount.y
            }
        }
        .onSizeChanged { topHeight = it.height }

    val bottomComposableModifier = Modifier

    Column(
        modifier
            .offset {
                IntOffset(
                    0,
                    offsetY.roundToInt()
                )
            }
            .onSizeChanged {
                columnWidth = it.width
                Log.d("hugh", "column width : $columnWidth")
            }
    ) {
        topComposable(Modifier)
        bottomComposable(bottomComposableModifier)
    }
}


@Preview
@Composable
fun ListPopupPreview() {
    val onClickRotation: () -> Unit = {}
    val onClickChatUiType: () -> Unit = {}
    val state = ContainerState(
        ChatUiType.Right
    )

    ListPopupComposable(
        topComposable = {
            PlayerScreen(
                modifier = it
                    .width(540.dp),
                state = state,
                isPortrait = true,
                onClickRotation = onClickRotation,
                onClickChatUiType = onClickChatUiType,
            )
        },

        bottomComposable = {
            ChattingScreen(
                modifier = it.fillMaxSize(),
                state = state,
                onClickRotation = onClickRotation,
                onClickChatUiType = onClickChatUiType,
            )
        }
    )
}
