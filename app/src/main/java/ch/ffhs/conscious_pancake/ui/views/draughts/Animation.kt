package ch.ffhs.conscious_pancake.ui.views.draughts

import ch.ffhs.conscious_pancake.draughts.model.core.Vector2

sealed class Animation {

    data class MoveAnimation(val from: Vector2, val to: Vector2): Animation()
}
