package ch.ffhs.conscious_pancake.draughts.model.core

import ch.ffhs.conscious_pancake.draughts.model.enums.PieceType

class Piece(
    val type: PieceType
) {
    var isDraught: Boolean = false

    override fun toString(): String {
        return type.toString()
    }
}