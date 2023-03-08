package com.example.playerscreentest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ContainerScreen() {
    PortraitPlayer()
}

@Composable
fun PortraitPlayer() {
    Column {
        PlayerScreen(
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(16 / 9f),
        )

        ChattingScreen(modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun PlayerScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color.Blue)
    ) {

    }
}

@Composable
fun ChattingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color.Green),
    ) { }
}

@Preview(device = "spec:orientation=portrait")
@Composable
fun PortraitPreView() {
    ContainerScreen()
}

@Preview(device = "spec:orientation=landscape")
@Composable
fun LandscapePreView() {
    ContainerScreen()
}
