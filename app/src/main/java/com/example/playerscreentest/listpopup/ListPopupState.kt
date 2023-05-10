package com.example.playerscreentest.listpopup

sealed class ListPopupState {
    object Minimized : ListPopupState()
    object Maximized : ListPopupState()

    companion object {
        class Test()
    }
}
