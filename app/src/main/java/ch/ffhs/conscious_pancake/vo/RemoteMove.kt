package ch.ffhs.conscious_pancake.vo

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.jorgeparavicini.draughts.model.core.Move
import com.jorgeparavicini.draughts.model.core.Vector2
import com.jorgeparavicini.draughts.model.enums.Player

data class RemoteMove(
    @get:Exclude val from: Vector2,
    @get:Exclude val to: Vector2,
    val player: Player,
    val didEat: Boolean
) {
    val fromX: Int
        get() = from.x

    val fromY: Int
        get() = from.y

    val toX: Int
        get() = to.x

    val toY: Int
        get() = to.y

    companion object {
        fun from(snapshot: DocumentSnapshot): RemoteMove {
            val from = Vector2(snapshot.get("fromX") as Int, snapshot.get("fromY") as Int)
            val to = Vector2(snapshot.get("toX") as Int, snapshot.get("toY") as Int)
            val player = Player.valueOf(snapshot.getString("player")!!)
            val didEat = snapshot.getBoolean("didEat")!!
            return RemoteMove(from, to, player, didEat)
        }

        fun from(hashMap: HashMap<String, Any>): RemoteMove {
            val from =
                Vector2((hashMap["fromX"]!! as Long).toInt(), (hashMap["fromY"]!! as Long).toInt())
            val to = Vector2((hashMap["toX"]!! as Long).toInt(), (hashMap["toY"]!! as Long).toInt())
            val player = Player.valueOf(hashMap["player"]!! as String)
            val didEat = hashMap["didEat"]!! as Boolean
            return RemoteMove(from, to, player, didEat)
        }
    }
}