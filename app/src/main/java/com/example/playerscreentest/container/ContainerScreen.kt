package com.example.playerscreentest.container

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playerscreentest.container.domain.model.ChatUiType

@Composable
fun ContainerScreen(
    containerViewModel: ContainerViewModel,
) {
    val state = containerViewModel.state.collectAsState()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val onClickRotation: () -> Unit = {
        containerViewModel.onToggleRotation()
    }
    val onClickChatUi: () -> Unit = {
        containerViewModel.nextChatUiType()
    }

    Log.d("hugh", "isPortrait : ${isPortrait}")

    if (isPortrait) {
        PortraitPlayer(
            state = state.value,
            isPortrait = isPortrait,
            onClickChatUiType = onClickChatUi,
            onClickRotation = onClickRotation,
        )
    } else {
        LandscapeChatRightPlayer(
            state = state.value,
            isPortrait = isPortrait,
            screenWidth = configuration.screenWidthDp,
            screenHeight = configuration.screenHeightDp,
            onClickChatUiType = onClickChatUi,
            onClickRotation = onClickRotation,
        )
    }
}

@Composable
fun PortraitPlayer(
    modifier: Modifier = Modifier,
    state: ContainerState,
    isPortrait: Boolean,
    onClickRotation: () -> Unit,
    onClickChatUiType: () -> Unit,
) {
    Column {
        PlayerScreen(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f),
            state = state,
            isPortrait = isPortrait,
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LandscapeChatRightPlayer(
    modifier: Modifier = Modifier,
    state: ContainerState,
    isPortrait: Boolean,
    screenWidth: Int,
    screenHeight: Int,
    onClickRotation: () -> Unit,
    onClickChatUiType: () -> Unit,
) {
    val chatUiTransition = updateTransition(state.chatUiType, label = "chat ui state")

    val chatWidth by chatUiTransition.animateDp(label = "chat width") {
        if (it is ChatUiType.Right) {
            260.dp
        } else {
            0.dp
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        PlayerScreen(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(16 / 9f)
                .heightIn(max = screenHeight.dp),
            state = state,
            isPortrait = isPortrait,
            onClickRotation = onClickRotation,
            onClickChatUiType = onClickChatUiType,
        )

        ChattingScreen(
            modifier = Modifier
                .width(chatWidth)
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
    isPortrait: Boolean,
    onClickRotation: () -> Unit,
    onClickChatUiType: () -> Unit,
) {
    Box(
        modifier = modifier.background(Color.Blue),
    ) {
        PlayerController(
            modifier = Modifier.align(Alignment.BottomEnd),
            state = state,
            isPortrait = isPortrait,
            onClickRotation = onClickRotation,
            onClickChatUiType = onClickChatUiType,
        )
    }
}

@Composable
fun PlayerController(
    modifier: Modifier = Modifier,
    state: ContainerState,
    isPortrait: Boolean,
    onClickRotation: () -> Unit,
    onClickChatUiType: () -> Unit,
) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Gray)
                .clickable {
                    onClickRotation.invoke()
                },
            text = "화면 회전",
        )

        Spacer(modifier = Modifier.size(10.dp))

        if (!isPortrait) {
            Text(
                modifier = Modifier
                    .wrapContentSize()
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

@Preview(device = "spec:orientation=portrait,width=411dp,height=891dp")
@Composable
fun PortraitPreView() {
    PortraitPlayer(
        state = ContainerState(ChatUiType.Right),
        isPortrait = true,
        onClickChatUiType = {},
        onClickRotation = {},
    )
}

@Preview(device = "spec:orientation=landscape,width=411dp,height=891dp")
@Composable
fun LandscapePreView() {
    LandscapeChatRightPlayer(
        state = ContainerState(ChatUiType.Right),
        isPortrait = false,
        screenWidth = 411,
        screenHeight = 891,
        onClickChatUiType = {},
        onClickRotation = {},
    )
}
