package ch.ffhs.conscious_pancake.ui.views.animation

import android.animation.TimeInterpolator
import android.view.animation.AccelerateDecelerateInterpolator
import ch.ffhs.conscious_pancake.ui.views.draughts.Coordinates

class CoordinatesAnimation(
    from: Coordinates,
    to: Coordinates,
    duration: Long = 1000,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
) : Animation<Coordinates>(from, to, duration, interpolator) {

    override val delta: Coordinates
        get() = (to - from) * animationDelta
}