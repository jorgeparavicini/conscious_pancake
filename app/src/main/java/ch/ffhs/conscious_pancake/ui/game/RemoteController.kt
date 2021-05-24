package ch.ffhs.conscious_pancake.ui.game

import com.jorgeparavicini.draughts.controllers.Controller
import com.jorgeparavicini.draughts.model.core.Move
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume

class RemoteController : Controller() {
    private var moveHandler: MoveHandler? = null


    override suspend fun getMove(): Move = suspendCancellableCoroutine { ctx ->
        Timber.v("Waiting for remote move")
        moveHandler = { move ->
            Timber.v("Trying to execute remote move $move")
            ctx.resume(move)
        }
    }

    fun onMoveExecuted(move: Move) {
        moveHandler?.invoke(move)
    }
}