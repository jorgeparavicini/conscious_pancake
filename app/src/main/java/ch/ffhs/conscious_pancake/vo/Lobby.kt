package ch.ffhs.conscious_pancake.vo

import androidx.databinding.Bindable
import ch.ffhs.conscious_pancake.BR
import ch.ffhs.conscious_pancake.vo.enums.GameSize
import ch.ffhs.conscious_pancake.vo.enums.PartyType
import ch.ffhs.conscious_pancake.vo.enums.TurnDuration
import com.google.firebase.firestore.Exclude

class Lobby(): BaseModel() {
    @Exclude
    @get:Bindable
    var id: String = ""
    set(value) {
        val old = field
        field = value
        applyChanges(old, value, BR.id)
    }

    @get:Bindable
    var partyType: PartyType = PartyType.OPEN
    set(value) {
        val old = field
        field = value
        applyChanges(old, value, BR.partyType)
    }

    @get:Bindable
    var gameSize: GameSize = GameSize.SIZE_8X8
    set(value) {
        val old = field
        field = value
        applyChanges(old, value, BR.gameSize)
    }

    @get:Bindable
    var turnDuration: TurnDuration = TurnDuration.H24
    set(value) {
        val old = field
        field = value
        applyChanges(old, value, BR.turnDuration)
    }

    @get:Bindable
    var player1Id: String = ""
    set(value) {
        val old = field
        field = value
        applyChanges(old, value, BR.player1Id)
    }

    @get:Bindable
    var player2Id: String? = null
    set(value) {
        val old = field
        field = value
        applyChanges(old, value, BR.player2Id)
    }

    @get:Bindable
    @get:Exclude
    var player1: User? = null
        set (value) {
            val old = field
            field = value
            applyChanges(old, value, BR.player1)
        }

    @get:Bindable
    @get:Exclude
    var player2: User? = null
        set (value) {
            val old = field
            field = value
            applyChanges(old, value, BR.player2)
        }

    constructor(player1Id: String) : this() {
        this.player1Id = player1Id
    }
}
