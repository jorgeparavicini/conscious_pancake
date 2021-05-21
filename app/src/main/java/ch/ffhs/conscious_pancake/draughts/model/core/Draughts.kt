package ch.ffhs.conscious_pancake.draughts.model.core

import ch.ffhs.conscious_pancake.draughts.controllers.Controller
import ch.ffhs.conscious_pancake.draughts.model.enums.PieceType
import ch.ffhs.conscious_pancake.draughts.model.exceptions.IllegalMoveException
import timber.log.Timber

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

    var hasStarted: Boolean = false
        private set

    var turn: Int = 0
        private set

    val currentTurnsPlayer: PieceType
        get() = if (turn % 2 == 0) PieceType.BLACK else PieceType.WHITE

    val blackPieces: List<Vector2>
        get() = this.field.getCellsWithPieces(PieceType.BLACK).map { it.position }

    val whitePieces: List<Vector2>
        get() = this.field.getCellsWithPieces(PieceType.WHITE).map { it.position }

    val currentController: Controller
        get() = if (currentTurnsPlayer == PieceType.BLACK) blackController else whiteController

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
        turn = 0
        field.reset()
        hasStarted = false
    }

    fun startGame() {
        if (hasStarted) throw IllegalStateException("Game already started")
        hasStarted = true
    }

    suspend fun nextTurn() {
        if (isGameOver) throw IllegalStateException("Game is already over")
        playTurn(currentController)
        turn += 1
    }

    private suspend fun playTurn(controller: Controller) {
        if (isGameOver) throw IllegalStateException("Game is already over")

        while (true) {
            if (isGameOver) break
            val move = controller.getMove(currentTurnsPlayer)
            try {
                if (!field.executeMove(move)) break
            } catch (e: IllegalMoveException) {
                Timber.v("Move was not allowed: $move")
                controller.illegalMove(move, e.message)
            }
        }
    }
}