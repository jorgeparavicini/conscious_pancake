package ch.ffhs.conscious_pancake.ui.game

import com.jorgeparavicini.draughts.controllers.Controller
import com.jorgeparavicini.draughts.model.core.Move
import com.jorgeparavicini.draughts.model.core.Piece
import com.jorgeparavicini.draughts.model.core.Vector2
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

typealias PieceSelectionChangedHandler = (Piece, Boolean) -> Unit
typealias MoveHandler = (Move) -> Unit


class PlayerController : Controller() {

    var onPieceSelectionChanged: PieceSelectionChangedHandler? = null

    private var moveHandler: MoveHandler? = null
    private val isWaitingForMove
        get() = moveHandler != null

    private var selectedPiece: Piece? = null

    override suspend fun getMove(): Move = suspendCancellableCoroutine { ctx ->
        moveHandler = { move ->
            Timber.v("Trying to execute move $move")
            resetSelections()
            ctx.resume(move)
        }
    }

    fun onPieceClicked(piece: Piece) {
        if (!isWaitingForMove) {
            Timber.w("Unrecognized click received")
            return
        }

        when {
            piece.player != player -> {
                Timber.e("Can not select enemy piece")
            }
            selectedPiece == piece -> {
                selectedPiece = null
                onPieceSelectionChanged?.invoke(piece, false)
            }
            selectedPiece == null -> {
                selectedPiece = piece
                onPieceSelectionChanged?.invoke(piece, true)
            }
            else -> {
                val old = selectedPiece!!
                selectedPiece = piece
                onPieceSelectionChanged?.invoke(old, false)
                onPieceSelectionChanged?.invoke(piece, true)
            }
        }
    }

    fun onCellClicked(pos: Vector2) {
        if (!isWaitingForMove) {
            Timber.w("Unrecognized click received")
            return
        }

        if (selectedPiece == null) return
        moveHandler?.invoke(Move(selectedPiece!!, pos))
    }

    private fun resetSelections() {
        onPieceSelectionChanged?.invoke(selectedPiece!!, false)
        moveHandler = null
        selectedPiece = null
    }
}