package ch.ffhs.conscious_pancake.draughts.model.core

class Cell(val position: Vector2, piece: Piece? = null) {
    var piece: Piece? = piece
        private set

    val isOccupied: Boolean
        get() = piece != null

    fun clear() {
        piece = null
    }

    fun setPiece(newPiece: Piece) {
        piece = newPiece
    }

    override fun toString(): String {
        return piece?.toString() ?: "-"
    }
}