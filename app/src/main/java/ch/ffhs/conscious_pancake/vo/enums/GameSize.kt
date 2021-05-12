package ch.ffhs.conscious_pancake.vo.enums

enum class GameSize(val displayName: String, val index: Int) {

    SIZE_8X8("8x8", 0),
    SIZE_6X6("6x6", 1);

    override fun toString(): String {
        return displayName
    }

    companion object {
        private val map = values().associateBy(GameSize::index)
        fun fromIndex(index: Int) = map[index]
    }
}