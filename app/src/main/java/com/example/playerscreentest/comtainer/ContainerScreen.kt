package com.example.playerscreentest

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playerscreentest.comtainer.ContainerState
import com.example.playerscreentest.comtainer.ContainerViewModel

@Composable
fun ContainerScreen(
    containerViewModel: ContainerViewModel,
) {
    val state = containerViewModel.state.collectAsState()
    val onClickRotation: () -> Unit = {
        containerViewModel.onToggleRotation()
    }
    val onClickChatUi: () -> Unit = {
        containerViewModel.nextChatUiType()
    }

    Log.d("hugh", "isPortrait : ${state.value.isPortrait}")
    if (state.value.isPortrait) {
        PortraitPlayer(
            state = state.value,
            onClickChatUiType = onClickChatUi,
            onClickRotation = onClickRotation,
        )
    } else {
        LandscapePlayer(
            state = state.value,
            onClickChatUiType = onClickChatUi,
            onClickRotation = onClickRotation,
        )
    }
}

@Composable
fun PortraitPlayer(
    modifier: Modifier = Modifier,
    state: ContainerState,
    onClickRotation: () -> Unit,
    onClickChatUiType: () -> Unit,
) {
    Column {
        PlayerScreen(
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(16 / 9f),
            state = state,
            onClickRotation = onClickRotation,
            onClickChatUiType = onClickChatUiType,
        )

        ChattingScreen(
            modifier = Modifier.fillMaxSize(),
            state = state,
            onClickRotation = onClickRotation,
            onClickChatUiType = onClickChatUiType,
        )
    }
}

@Composable
fun LandscapePlayer(
    modifier: Modifier = Modifier,
    state: ContainerState,
    onClickRotation: () -> Unit,
    onClickChatUiType: () -> Unit,
) {
    Row {
        PlayerScreen(
            modifier = Modifier.fillMaxHeight()
                .aspectRatio(16 / 9f),
            state = state,
            onClickRotation = onClickRotation,
            onClickChatUiType = onClickChatUiType,
        )

        ChattingScreen(
            modifier = Modifier.width(100.dp)
                .fillMaxHeight(),
            state = state,
            onClickRotation = onClickRotation,
            onClickChatUiType = onClickChatUiType,
        )
    }
}

@Composable
fun PlayerScreen(
    modifier: Modifier = Modifier,
    state: ContainerState,
    onClickRotation: () -> Unit,
    onClickChatUiType: () -> Unit,
) {
    Box(
        modifier = modifier.background(Color.Blue),
    ) {
        PlayerController(
            modifier = Modifier.align(Alignment.BottomEnd),
            state = state,
            onClickRotation = onClickRotation,
            onClickChatUiType = onClickChatUiType,
        )
    }
}

@Composable
fun PlayerController(
    modifier: Modifier = Modifier,
    state: ContainerState,
    onClickRotation: () -> Unit,
    onClickChatUiType: () -> Unit,
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier.wrapContentSize()
                .background(Color.Gray)
                .clickable {
                    onClickRotation.invoke()
                },
            text = "화면 회전",
        )

        Spacer(modifier = Modifier.size(10.dp))

        if (!state.isPortrait) {
            Text(
                modifier = Modifier.wrapContentSize()
                    .background(Color.Gray)
                    .clickable {
                        onClickChatUiType.invoke()
                    },
                text = "채팅모드 변경",
            )
        }
    }
}

@Composable
fun ChattingScreen(
    modifier: Modifier = Modifier,
    state: ContainerState,
    onClickRotation: () -> Unit,
    onClickChatUiType: () -> Unit,
) {
    Box(
        modifier = modifier
            .background(Color.Green),
    ) { }
}

@Preview(device = "spec:orientation=portrait")
@Composable
fun PortraitPreView() {
//    ContainerScreen()
}

@Preview(device = "spec:orientation=landscape")
@Composable
fun LandscapePreView() {
//    ContainerScreen()
}
