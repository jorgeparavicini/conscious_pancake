package ch.ffhs.conscious_pancake.vo

import androidx.databinding.Bindable
import ch.ffhs.conscious_pancake.BR
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.QueryDocumentSnapshot

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
            applyChanges(old, value, BR.lastAction)
        }

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
    val players: List<String>
        get() = listOf(hostId, player2Id)

    val gameOver: Boolean
        get() = winner != null

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromFirebase(snapshot: QueryDocumentSnapshot): Game {
            val obj = Game()
            obj.id = snapshot.id
            obj.lastAction = snapshot.getTimestamp("lastAction")!!
            obj.turn = snapshot.getLong("turn")!!.toInt()
            obj.winner = snapshot.getString("winner")
            val players = snapshot.get("players") as List<String>
            obj.hostId = players[0]
            obj.player2Id = players[1]
            return obj
        }
    }
}
