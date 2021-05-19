package ch.ffhs.conscious_pancake.ui.game

import ch.ffhs.conscious_pancake.draughts.controllers.Controller
import ch.ffhs.conscious_pancake.draughts.model.core.Move
import ch.ffhs.conscious_pancake.draughts.model.core.Vector2
import ch.ffhs.conscious_pancake.draughts.model.enums.PieceType

class PlayerController: Controller() {
    override suspend fun getMove(player: PieceType): Move {
        return Move(Vector2.zero(), Vector2.one(), player)
    }
}