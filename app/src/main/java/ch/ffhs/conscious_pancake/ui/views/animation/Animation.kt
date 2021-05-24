package ch.ffhs.conscious_pancake.ui.views.animation

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator

typealias OnAnimationOverListener = () -> Unit
typealias OnUpdateListener = () -> Unit

abstract class Animation<T>(
    val from: T,
    val to: T,
    duration: Long = 1000,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()
) {
    private var animationOverListener: OnAnimationOverListener? = null
    private var updateListener: OnUpdateListener? = null

    private var lastValue = 0f

    private var isDone: Boolean = false
        private set(value) {
            if (field == value) return
            field = value
            if (isDone) animationOverListener?.invoke()
        }

    val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
        this.duration = duration
        this.interpolator = interpolator
        this.addUpdateListener {
            updateListener?.invoke()
            if (it.animatedValue as Float >= 1) {
                isDone = true
            }
            lastValue = animatedValue as Float
        }
    }

    val animationDelta: Float
        get() = animator.animatedValue as Float - lastValue

    abstract val delta: T

    fun setOnAnimationOverListener(listener: OnAnimationOverListener) {
        animationOverListener = listener
    }

    fun setOnUpdateListener(listener: OnUpdateListener) {
        updateListener = listener
    }
}
