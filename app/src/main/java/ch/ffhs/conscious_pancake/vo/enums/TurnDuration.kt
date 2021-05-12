package ch.ffhs.conscious_pancake.vo.enums

enum class TurnDuration(val displayName: String, val index: Int) {

    H24("24H", 0),
    H12("12H", 1),
    H8("8H", 2),
    H4("4H", 3),
    H2("2H", 4);

    override fun toString(): String {
        return displayName
    }

    companion object {
        private val map = values().associateBy(TurnDuration::index)
        fun fromIndex(index: Int) = map[index]
    }
}