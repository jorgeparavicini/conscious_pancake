package ch.ffhs.conscious_pancake.draughts.model.enums

import ch.ffhs.conscious_pancake.draughts.model.core.Vector2

enum class Direction(val direction: Vector2) {
    TOP_RIGHT(Vector2(1, 1)),
    BOTTOM_RIGHT(Vector2(1, -1)),
    BOTTOM_LEFT(Vector2(-1, -1)),
    TOP_LEFT(Vector2(-1, 1)),
    NONE(Vector2.zero());
}