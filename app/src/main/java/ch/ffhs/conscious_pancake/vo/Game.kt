package ch.ffhs.conscious_pancake.vo

import androidx.databinding.Bindable
import ch.ffhs.conscious_pancake.BR
import ch.ffhs.conscious_pancake.vo.enums.GameSize
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

open class Game : BaseModel() {

    @get:Bindable
    @get:Exclude
    var id: String = ""
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.id)
        }

    @get:Bindable
    @get:Exclude
    var hostId: String = ""
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.hostId)
        }

    @get:Bindable
    @get:Exclude
    var player2Id: String = ""
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.player2Id)
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
            applyChanges(old, value, BR.lastAction)
        }

    @get:Bindable
    var gameSize: GameSize = GameSize.SIZE_8X8
        set(value) {
            val old = field
            field = value
            applyChanges(old, value, BR.gameSize)
        }

    var remoteMoves: MutableList<RemoteMove> = mutableListOf()
        private set

    @get:Bindable
    @get:Exclude
    var player1: User? = null
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

    // Used implicitly by firebase
    @Suppress("unused")
    val players: List<String>
        get() = listOf(hostId, player2Id)

    val gameOver: Boolean
        get() = winner != null

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromFirebase(snapshot: DocumentSnapshot): Game {
            return Game().apply {
                id = snapshot.id
                lastAction = snapshot.getTimestamp("lastAction")!!
                winner = snapshot.getString("winner")
                val players = snapshot.get("players") as List<String>
                hostId = players[0]
                player2Id = players[1]
                gameSize = GameSize.valueOf(snapshot.getString("gameSize")!!)
                remoteMoves =
                    (snapshot.get("remoteMoves") as List<HashMap<String, Any>>).map { hashMap ->
                        RemoteMove.from(hashMap)
                    }.toMutableList()
            }
        }
    }
}
