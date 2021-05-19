package ch.ffhs.conscious_pancake.draughts.model.enums

enum class PieceType(private val displayValue: String) {
    BLACK("■"),
    WHITE("□");

    override fun toString(): String {
        return displayValue
    }
}