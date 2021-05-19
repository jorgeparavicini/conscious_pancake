package ch.ffhs.conscious_pancake.draughts.model.core

import ch.ffhs.conscious_pancake.draughts.model.enums.PieceType

data class Move(val from: Vector2, val to: Vector2, val player: PieceType)
