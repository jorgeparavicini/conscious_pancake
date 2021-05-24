package ch.ffhs.conscious_pancake.ui.game

import androidx.lifecycle.*
import com.jorgeparavicini.draughts.model.core.Draughts
import com.jorgeparavicini.draughts.model.core.GameOverHandler
import com.jorgeparavicini.draughts.model.core.GameResetHandler
import com.jorgeparavicini.draughts.model.core.Vector2
import com.jorgeparavicini.draughts.model.enums.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    val gameId = savedStateHandle.get<String>("gameId")!!

    val isHost = savedStateHandle.get<Boolean>("isHost")!!

    private val hostController = PlayerController()

    private val player2Controller = PlayerController()

    val draughts = Draughts(hostController, player2Controller)

    private val _turnLabel = MutableLiveData<String>()
    val turnLabel: LiveData<String>
        get() = _turnLabel

    init {
        viewModelScope.launch {
            draughts.startGame()
            while (!draughts.isGameOver) {
                updateTurnLabel()
                draughts.nextTurn()
            }
        }

        draughts.field.setOnGameOverHandler {
            _turnLabel.value = if (it == Player.BLACK) "Black Won" else "White Won"
        }
    }

    fun onCellClicked(pos: Vector2) {
        // Only propagate click event if the current controller is actually a player controller
        val controller = draughts.currentController as? PlayerController ?: return
        val piece = draughts.field.getPiece(pos)
        if (piece != null && !piece.eaten) {
            controller.onPieceClicked(piece)
        } else {
            controller.onCellClicked(pos)
        }
    }

    fun setOnPieceSelectionChangedHandler(handler: PieceSelectionChangedHandler) {
        (hostController as? PlayerController)?.onPieceSelectionChanged = handler
        (player2Controller as? PlayerController)?.onPieceSelectionChanged = handler
    }

    fun setOnGameResetHandler(handler: GameResetHandler) {
        draughts.field.setOnGameResetHandler(handler)
    }

    private fun updateTurnLabel() {
        if (isHost && draughts.currentPlayer == Player.BLACK) {
            _turnLabel.value = "Your turn"
        } else {
            _turnLabel.value = "Enemies turn"
        }
    }
}