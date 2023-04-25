package com.example.playerscreentest.listpopup

interface ListPopupEdge

sealed interface VerticalEdge : ListPopupEdge {
    fun reverse(): VerticalEdge

    object Top : VerticalEdge {
        override fun reverse(): VerticalEdge {
            return Bottom
        }
    }

    object Bottom : VerticalEdge {
        override fun reverse(): VerticalEdge {
            return Top
        }
    }
}

sealed interface HorizontalEdge : ListPopupEdge {
    fun reverse(): HorizontalEdge

    object Left : HorizontalEdge {
        override fun reverse(): HorizontalEdge {
            return Right
        }
    }

    object Right : HorizontalEdge {
        override fun reverse(): HorizontalEdge {
            return Left
        }
    }
}

data class ListPopupPosition(val verticalEdge: VerticalEdge, val horizontalEdge: HorizontalEdge)

fun ListPopupPosition.reversePopupEdge(
    isReverseX: Boolean,
    isReverseY: Boolean
): ListPopupPosition {
    return copy(
        horizontalEdge = if (isReverseX) horizontalEdge.reverse() else horizontalEdge,
        verticalEdge = if (isReverseY) verticalEdge.reverse() else verticalEdge
    )
}