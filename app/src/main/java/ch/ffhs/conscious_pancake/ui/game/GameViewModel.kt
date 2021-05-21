package ch.ffhs.conscious_pancake.ui.game

import androidx.lifecycle.*
import ch.ffhs.conscious_pancake.draughts.controllers.Controller
import ch.ffhs.conscious_pancake.draughts.model.core.*
import ch.ffhs.conscious_pancake.draughts.model.enums.PieceType
import ch.ffhs.conscious_pancake.vo.Game
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
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

    var onBlackCellSelectionChangedListener: OnCellSelectionChangedListener?
        get() = (hostController as? PlayerController)?.onCellSelectionChangedListener
        set(value) {
            (hostController as? PlayerController)?.onCellSelectionChangedListener = value
        }

    var onWhiteCellSelectionChangedListener: OnCellSelectionChangedListener?
        get() = (player2Controller as? PlayerController)?.onCellSelectionChangedListener
        set(value) {
            (player2Controller as? PlayerController)?.onCellSelectionChangedListener = value
        }

    var onPieceMovedListener: OnPieceMovedListener?
        get() = draughts.onPieceMovedListener
        set(value) {
            draughts.onPieceMovedListener = value
        }

    var onPieceEatenListener: OnPieceEatenListener?
        get() = draughts.onPieceEatenListener
        set(value) {
            draughts.onPieceEatenListener = value
        }

    var onPiecePromotedListener: OnPiecePromotedListener?
        get() = draughts.onPiecePromotedListener
        set(value) {
            draughts.onPiecePromotedListener = value
        }

    var onGameOverListener: OnGameOverListener?
        get() = draughts.onGameOverListener
        set(value) {
            draughts.onGameOverListener = value
        }

    var onGameResetListener: OnGameResetListener?
        get() = draughts.onGameResetListener
        set(value) {
            draughts.onGameResetListener = value
        }

    init {
        viewModelScope.launch {
            draughts.startGame()
            while (!draughts.isGameOver) {
                updateTurnLabel()
                draughts.nextTurn()
            }
        }
    }

    fun onCellClicked(pos: Vector2) {
        // Only propagate click event if the current controller is actually a player controller
        Timber.v("Cell $pos clicked")
        val controller = draughts.currentController as? PlayerController ?: return

        controller.onCellClicked(pos)
    }

    private fun updateTurnLabel() {
        if (isHost && draughts.currentTurnsPlayer == PieceType.BLACK) {
            _turnLabel.value = "Your turn"
        } else {
            _turnLabel.value = "Enemies turn"
        }
    }
}