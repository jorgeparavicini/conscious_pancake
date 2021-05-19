package ch.ffhs.conscious_pancake.draughts.model.core

import ch.ffhs.conscious_pancake.draughts.controllers.Controller
import ch.ffhs.conscious_pancake.draughts.model.enums.PieceType
import ch.ffhs.conscious_pancake.draughts.model.exceptions.IllegalMoveException

class Draughts(
    private val blackController: Controller,
    private val whiteController: Controller,
    val size: Int = 8
) {
    val field = Field(size)

    val winner: PieceType?
        get() = this.field.winner

    val isGameOver: Boolean
        get() = this.field.isGameOver

    val turn: Int
        get() = this.field.turn

    val currentTurnsPlayer: PieceType
        get() = this.field.currentTurnsPlayer

    var onPieceMovedListener: OnPieceMovedListener?
        get() = this.field.onPieceMovedListener
        set(value) {
            this.field.onPieceMovedListener = value
        }

    var onPieceEatenListener: OnPieceEatenListener?
        get() = this.field.onPieceEatenListener
        set(value) {
            this.field.onPieceEatenListener = value
        }

    var onPiecePromotedListener: OnPiecePromotedListener?
        get() = this.field.onPiecePromotedListener
        set(value) {
            this.field.onPiecePromotedListener = value
        }

    var onGameOverListener: OnGameOverListener?
        get() = this.field.onGameOverListener
        set(value) {
            this.field.onGameOverListener = value
        }

    var onGameResetListener: OnGameResetListener?
        get() = this.field.onGameResetListener
        set(value) {
            this.field.onGameResetListener = value
        }

    fun reset() {
        field.reset()
    }

    suspend fun nextTurn() {
        if (isGameOver) throw IllegalStateException("Game is already over")
        val controller =
            if (field.currentTurnsPlayer == PieceType.BLACK) blackController else whiteController

        playTurn(controller)
    }

    private suspend fun playTurn(controller: Controller) {
        if (isGameOver) throw IllegalStateException("Game is already over")

        while (true) {
            if (isGameOver) break
            val move = controller.getMove(field.currentTurnsPlayer)
            try {
                if (!field.executeMove(move)) break
            } catch (e: IllegalMoveException) {
                controller.illegalMove(move, e.message)
            }
        }
    }
}