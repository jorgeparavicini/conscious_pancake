package ch.ffhs.conscious_pancake.ui.views.draughts

import com.jorgeparavicini.draughts.model.core.Vector2

data class Coordinates(val x: Float, val y: Float) {
    operator fun plus(other: Coordinates): Coordinates {
        return Coordinates(x + other.x, y + other.y)
    }

    operator fun plus(scalar: Float): Coordinates {
        return Coordinates(x + scalar, y + scalar)
    }

    operator fun minus(other: Coordinates): Coordinates {
        return Coordinates(x - other.x, y - other.y)
    }

    operator fun times(scalar: Float): Coordinates {
        return Coordinates(x * scalar, y * scalar)
    }

    companion object {
        fun from(position: Vector2): Coordinates {
            return Coordinates(position.x.toFloat(), position.y.toFloat())
        }
    }
}