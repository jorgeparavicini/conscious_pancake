package ch.ffhs.conscious_pancake.ui.game

import androidx.lifecycle.*
import ch.ffhs.conscious_pancake.repository.GameRepository
import ch.ffhs.conscious_pancake.vo.Game
import ch.ffhs.conscious_pancake.vo.RemoteMove
import ch.ffhs.conscious_pancake.vo.enums.Status
import com.jorgeparavicini.draughts.controllers.Controller
import com.jorgeparavicini.draughts.model.core.Draughts
import com.jorgeparavicini.draughts.model.core.GameResetHandler
import com.jorgeparavicini.draughts.model.core.Move
import com.jorgeparavicini.draughts.model.core.Vector2
import com.jorgeparavicini.draughts.model.enums.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepo: GameRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val gameId = savedStateHandle.get<String>("gameId")!!

    val isHost = savedStateHandle.get<Boolean>("isHost")!!

    private val hostController: Controller = if (isHost) PlayerController() else RemoteController()

    private val player2Controller: Controller =
        if (isHost) RemoteController() else PlayerController()

    private var gameResetHandler: GameResetHandler? = null

    val localPlayer = if (isHost) Player.BLACK else Player.WHITE

    var draughts: Draughts? = null
        private set(value) {
            if (field != null) {
                Timber.e("Game already set")
                return
            }
            field = value!!
            viewModelScope.launch {
                value.startGame()
                while (!value.isGameOver) {
                    updateTurnLabel()
                    value.nextTurn()
                }
            }
            value.field.setOnGameOverHandler {
                _turnLabel.value = if (it == Player.BLACK) "Black Won" else "White Won"
            }
        }

    val game = Transformations.map(gameRepo.getLiveGame(gameId)) {
        if (it.status == Status.ERROR) {
            Timber.e("Could not fetch lobbies")
            return@map null
        }

        if (draughts == null) {
            draughts =
                Draughts(
                    hostController,
                    player2Controller,
                    it.data!!.gameSize.fieldSize
                )

            it.data.remoteMoves.forEach { remoteMove ->
                val piece = draughts!!.field.getPiece(remoteMove.from)!!
                draughts!!.nextMove(Move(piece, remoteMove.to))
            }

            gameResetHandler?.let { handler -> draughts!!.field.setOnGameResetHandler(handler) }
            draughts!!.setOnMoveExecutedHandler { from, to, player ->
                onMoveExecuted(
                    from,
                    to,
                    player
                )
            }
            return@map it.data
        }

        onGameUpdated(it.data!!)

        it.data
    }

    private val _turnLabel = MutableLiveData<String>()
    val turnLabel: LiveData<String>
        get() = _turnLabel

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun onCellClicked(pos: Vector2) {
        // Only propagate click event if the current controller is actually a player controller
        val controller = draughts?.currentController as? PlayerController ?: return
        val piece = draughts?.field?.getPiece(pos)
        if (piece != null && !piece.eaten) {
            controller.onPieceClicked(piece)
        } else {
            controller.onCellClicked(pos)
        }
    }

    private fun onGameUpdated(newGame: Game) {
        val remoteController = draughts?.currentController as? RemoteController ?: return
        val lastMove = game.value?.remoteMoves?.lastOrNull()
        val newMove = newGame.remoteMoves.lastOrNull()
        if (lastMove != newMove && newMove != null) {
            if (newMove.player == localPlayer) return
            val piece = draughts!!.field.getPiece(newMove.from)!!
            remoteController.onMoveExecuted(Move(piece, newMove.to))
        }
    }

    fun setOnPieceSelectionChangedHandler(handler: PieceSelectionChangedHandler) {
        (hostController as? PlayerController)?.onPieceSelectionChanged = handler
        (player2Controller as? PlayerController)?.onPieceSelectionChanged = handler
    }

    fun setOnGameResetHandler(handler: GameResetHandler) {
        if (draughts == null) {
            gameResetHandler = handler
        } else {
            draughts!!.field.setOnGameResetHandler(handler)
        }
    }

    private fun onMoveExecuted(from: Vector2, to: Vector2, player: Player) {
        val localPlayer = if (isHost) Player.BLACK else Player.WHITE
        if (player != localPlayer) return
        viewModelScope.launch {
            gameRepo.addMoveToGame(gameId, RemoteMove(from, to, player))
        }
    }

    private fun updateTurnLabel() {
        if (isHost && draughts!!.currentPlayer == Player.BLACK ||
                !isHost && draughts!!.currentPlayer == Player.WHITE
        ) {
            _turnLabel.value = "Your turn"
        } else {
            _turnLabel.value = "Enemies turn"
        }
    }
}