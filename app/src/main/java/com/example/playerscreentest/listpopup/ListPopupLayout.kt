package com.example.playerscreentest.listpopup

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.annotation.Dimension
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class ListPopupLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle),
    GestureDetector.OnGestureListener,
    ScaleGestureDetector.OnScaleGestureListener {

    private lateinit var topView: View
    private lateinit var bottomView: View

    private var isMinimized: Boolean = false

    /**
     * bottom margin만 외부에서 변경가능하도록
     */
    var marginBottomPx = dpToPx(context, 112f)
    val marginTopPx = dpToPx(context, 10f)
    val marginRightPx = dpToPx(context, 10f)
    val marginLeftPx = dpToPx(context, 10f)

    private val gestureDetector by lazy {
        GestureDetectorCompat(context, this)
    }

    private val scaleDetector by lazy {
        ScaleGestureDetector(context, this)
    }

    private var canListPopup: () -> Boolean = { true }
    private var onFinish: () -> Unit = {}
    private var onChangeListPopupState: (ListPopupState) -> Unit = {}
    private val parentView: View by lazy { parent as View }

    private val defaultListPopupPosition = ListPopupPosition(
        VerticalEdge.Bottom,
        HorizontalEdge.Right
    )

    private var currentPopupPosition: ListPopupPosition = defaultListPopupPosition

    fun setCanListPopup(
        canListPopup: () -> Boolean
    ): ListPopupLayout {
        this.canListPopup = canListPopup
        return this
    }

    fun setOnFinish(onFinish: () -> Unit): ListPopupLayout {
        this.onFinish = onFinish
        return this
    }

    fun setListPopupMargin(
        @Dimension(unit = Dimension.DP)
        margin: Int = UNSET_MARGIN
    ): ListPopupLayout {
        marginBottomPx = dpToPx(context, margin.toFloat())
        return this
    }

    fun setOnChangedListPopupState(onChangeListPopupState: (ListPopupState) -> Unit): ListPopupLayout {
        this.onChangeListPopupState = onChangeListPopupState
        return this
    }

    fun setTopView(@IdRes topViewResId: Int) {
        topView = findViewById(topViewResId)
    }

    fun setBottomView(@IdRes bottomViewId: Int) {
        bottomView = findViewById(bottomViewId)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!canListPopup()) return super.onInterceptTouchEvent(ev)
        return handleTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!canListPopup()) return super.onTouchEvent(event)
        handleTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun handleTouchEvent(ev: MotionEvent): Boolean {
        if (!isTopViewArea(
                ev.x.roundToInt(),
                ev.y.roundToInt()
            ) || !scaleDetector.onTouchEvent(ev)
        ) {
            if (ev.actionMasked == MotionEvent.ACTION_UP) {
                handleActionUpEvent()
            }

            return false
        }

        val isConsumed = gestureDetector.onTouchEvent(ev)

        if (!isConsumed) {
            if (ev.actionMasked == MotionEvent.ACTION_UP) {
                handleActionUpEvent()
            }
        }

        return isConsumed
    }

    private fun handleActionUpEvent() {
        if (isMinimized) {
            if (isFinishFromDrag()) return

            moveToListPopupPosition(
                position = getListPopupPositionByScreen()
            )
        } else {
            if (topView.y > topView.measuredHeight / 3) {
                minimize()
            } else {
                returnToMaximizePosition()
            }
        }
    }

    private fun getListPopupPositionByScreen(): ListPopupPosition {
        val centerX = topView.x + (topView.width / 2)
        val centerY = topView.y + (topView.height / 2)

        val parentCenterX = parentView.width / 2
        val parentCenterY = parentView.height / 2

        return ListPopupPosition(
            verticalEdge = if (centerY < parentCenterY) {
                VerticalEdge.Top
            } else {
                VerticalEdge.Bottom
            },
            horizontalEdge = if (centerX < parentCenterX) {
                HorizontalEdge.Left
            } else {
                HorizontalEdge.Right
            }
        )
    }

    private fun returnToMaximizePosition() {
        topView.animate()
            .y(0f)
            .x(0f)
            .setDuration(RETURN_DURATION)
            .start()

        bottomView.animate()
            .translationX(0f)
            .translationY(0f)
            .setDuration(RETURN_DURATION)
            .start()
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {}

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return if (isMinimized) {
            maximize()
            true
        } else {
            false
        }
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return if (isMinimized) {
            topView.scroll(distanceX, distanceY)
            true
        } else {
            return if (topView.y - distanceY < 0) {
                topView.translationY = 0f
                bottomView.translationY = 0f
                false
            } else {
                scrollByMaximizedState(distanceY)
                true
            }
        }
    }

    private fun isFinishFromDrag(): Boolean {
        val isLeftEnd = topView.x + (topView.width / 3) < 0
        val isRightEnd = topView.x + ((topView.width / 3) * 2) > width

        fun movePopupViewForFinish(view: View, distanceX: Float) {
            view.post {
                animate()
                    .x(distanceX)
                    .setDuration(DRAG_FINISH_DURATION)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            onFinish.invoke()
                        }
                    })
                    .start()
            }
        }

        return if (isLeftEnd) {
            movePopupViewForFinish(topView, -1f * topView.width)
            true
        } else if (isRightEnd) {
            movePopupViewForFinish(topView, topView.width.toFloat())
            true
        } else {
            false
        }
    }

    override fun onLongPress(e: MotionEvent) {}

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (!isMinimized || isFinishFromDrag()) return false

        currentPopupPosition.run {
            val nextEdge = reversePopupEdge(
                isReverseX = velocityX.absoluteValue > MINIMUM_FLING_VELOCITY,
                isReverseY = velocityY.absoluteValue > MINIMUM_FLING_VELOCITY
            )

            moveToListPopupPosition(
                position = if (currentPopupPosition == nextEdge) {
                    getListPopupPositionByScreen()
                } else {
                    nextEdge
                }
            )
        }

        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return false
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {}

    private fun scrollByMaximizedState(distanceY: Float) {
        fun viewAnimateTranslationYBy(view: View, byY: Float) {
            view.animate()
                .translationYBy(byY)
                .setDuration(0)
                .start()
        }
        // View.translationY로 바로 조정하면 bottomView가 사라져 ValueAnimator 사용
        viewAnimateTranslationYBy(topView, -distanceY)
        viewAnimateTranslationYBy(bottomView, -distanceY)
    }

    private fun View.scroll(distanceX: Float, distanceY: Float) {
        animate()
            .translationXBy(-distanceX)
            .translationYBy(-distanceY)
            .setDuration(0)
            .start()
    }

    private fun isTopViewArea(hitX: Int, hitY: Int): Boolean {
        val topViewRect = Rect().apply {
            topView.getHitRect(this)
        }

        return topViewRect.contains(hitX, hitY)
    }

    /**
     * 리스트팝업 최소화
     */
    fun minimize() {
        if (isMinimized) return

        topView.updateLayoutParams {
            width = parentView.width / 2
        }

        bottomView.isVisible = false

        moveToListPopupPosition(defaultListPopupPosition)

        isMinimized = true

        onChangeListPopupState.invoke(ListPopupState.Minimized)
    }

    /**
     * 리스트팝업 최대화
     */
    fun maximize() {
        if (!isMinimized) return

        topView.updateLayoutParams {
            width = 0
        }

        bottomView.isVisible = true

        returnToMaximizePosition()

        isMinimized = false

        onChangeListPopupState.invoke(ListPopupState.Maximized)
    }

    private fun moveToListPopupPosition(position: ListPopupPosition) {
        val topViewWidth = width / 2
        val topViewHeight = (topViewWidth / 16) * 9

        SpringAnimation(
            topView,
            DynamicAnimation.X,
            when (position.horizontalEdge) {
                is HorizontalEdge.Left -> {
                    marginLeftPx.toFloat()
                }
                is HorizontalEdge.Right -> {
                    (topViewWidth - marginRightPx).toFloat()
                }
            }
        ).start()

        SpringAnimation(
            topView,
            DynamicAnimation.Y,
            when (position.verticalEdge) {
                is VerticalEdge.Top -> {
                    marginTopPx.toFloat()
                }
                is VerticalEdge.Bottom -> {
                    (height - topViewHeight - marginBottomPx).toFloat()
                }
            }
        ).start()

        currentPopupPosition = position
    }

    companion object {
        private const val UNSET_MARGIN = -1
        private const val MINIMUM_FLING_VELOCITY = 3000
        private const val DRAG_FINISH_DURATION = 150L
        private const val RETURN_DURATION = 100L
    }
}

fun dpToPx(context: Context, dp: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}