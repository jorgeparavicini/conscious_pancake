package ch.ffhs.conscious_pancake.vo

import androidx.databinding.Bindable
import ch.ffhs.conscious_pancake.BR
import com.google.firebase.Timestamp

class Game : BaseModel() {

    @get:Bindable
    var id: String = ""
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.id)
        }

    @get:Bindable
    var player1Id: String = ""
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.player1Id)
        }

    @get:Bindable
    var player2Id: String = ""
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.player2Id)
        }

    @get:Bindable
    var turn: Int = 0
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.turn)
        }

    @get:Bindable
    var winner: String? = null
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.winner)
        }

    @get:Bindable
    var lastAction: Timestamp = Timestamp.now()
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.player1Id)
        }
}
