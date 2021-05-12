package ch.ffhs.conscious_pancake.vo

import androidx.databinding.Bindable
import ch.ffhs.conscious_pancake.BR
import ch.ffhs.conscious_pancake.vo.enums.GameSize
import ch.ffhs.conscious_pancake.vo.enums.PartyType
import ch.ffhs.conscious_pancake.vo.enums.TurnDuration
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

class Lobby() : BaseModel() {

    @get:Bindable
    @get:Exclude
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
    var hostId: String = ""
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

    var createdAt: Timestamp = Timestamp.now()
        private set

    @get:Bindable
    @get:Exclude
    var host: User? = null
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.player1)
        }

    @get:Bindable
    @get:Exclude
    var player2: User? = null
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.player2)
        }

    constructor(hostId: String) : this() {
        this.hostId = hostId
    }

    companion object {

        fun fromFirebase(snapshot: DocumentSnapshot): Lobby {
            return Lobby().apply {
                id = snapshot.id
                partyType = PartyType.valueOf(snapshot.getString("partyType")!!)
                gameSize = GameSize.valueOf(snapshot.getString("gameSize")!!)
                turnDuration = TurnDuration.valueOf(snapshot.getString("turnDuration")!!)
                hostId = snapshot.getString("hostId")!!
                player2Id = snapshot.getString("player2Id")
                createdAt = snapshot.get("createdAt", Timestamp::class.java)!!
            }
        }
    }
}
