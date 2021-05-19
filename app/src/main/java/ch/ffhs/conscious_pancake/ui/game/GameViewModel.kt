package ch.ffhs.conscious_pancake.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ch.ffhs.conscious_pancake.draughts.controllers.Controller
import ch.ffhs.conscious_pancake.draughts.model.core.Draughts
import ch.ffhs.conscious_pancake.vo.Game
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    val gameId = savedStateHandle.get<String>("gameId")

    val draughts = Draughts(PlayerController(), PlayerController())
}