package ch.ffhs.conscious_pancake.ui.game

import ch.ffhs.conscious_pancake.draughts.controllers.Controller
import ch.ffhs.conscious_pancake.draughts.model.core.Move
import ch.ffhs.conscious_pancake.draughts.model.core.Vector2
import ch.ffhs.conscious_pancake.draughts.model.enums.PieceType
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

typealias OnMoveListener = (Move) -> Unit
typealias OnCellSelectionChangedListener = (Vector2, Boolean) -> Unit


class PlayerController : Controller() {

    var onCellSelectionChangedListener: OnCellSelectionChangedListener? = null

    private var onMoveListener: OnMoveListener? = null
    private val isWaitingForMove
        get() = onMoveListener != null

    private var from: Vector2? = null

    private var to: Vector2? = null

    private var player: PieceType? = null

    override suspend fun getMove(player: PieceType): Move = suspendCancellableCoroutine { ctx ->
        this.player = player
        onMoveListener = { move ->
            Timber.v("Trying to execute move $move")
            resetSelections()
            ctx.resume(move)
        }
    }

    fun onCellClicked(pos: Vector2) {
        if (!isWaitingForMove) {
            Timber.w("Unrecognized click received")
            return
        }
        when {
            from == null -> {
                from = pos
                Timber.v("Selected from position: $from")
                onCellSelectionChangedListener?.invoke(from!!, true)
            }
            to == null -> {
                to = pos
                Timber.v("Selected to position: $from")
                onMoveListener!!.invoke(Move(from!!, to!!, player!!))
            }
            else -> {
                throw IllegalStateException("Move should have already been executed, but wasn't")
            }
        }
    }

    private fun resetSelections() {
        onCellSelectionChangedListener?.invoke(from!!, false)
        onMoveListener = null
        from = null
        to = null
        player = null
    }
}