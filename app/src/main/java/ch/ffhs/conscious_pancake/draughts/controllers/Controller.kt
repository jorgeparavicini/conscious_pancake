package ch.ffhs.conscious_pancake.draughts.controllers

import ch.ffhs.conscious_pancake.draughts.model.core.Move
import ch.ffhs.conscious_pancake.draughts.model.enums.PieceType

abstract class Controller {
    abstract suspend fun getMove(player: PieceType): Move

    fun illegalMove(move: Move, message: String?) {}
}