package ch.ffhs.conscious_pancake.ui.views.animation

import android.animation.TimeInterpolator
import android.view.animation.AccelerateDecelerateInterpolator

class FloatAnimation(
    from: Float,
    to: Float,
    duration: Long = 1000,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator()
) : Animation<Float>(from, to, duration, interpolator) {

    override val delta: Float
        get() = (to - from) * animationDelta
}