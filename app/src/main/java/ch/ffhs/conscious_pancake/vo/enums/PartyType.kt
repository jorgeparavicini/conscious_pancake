package ch.ffhs.conscious_pancake.vo.enums

enum class PartyType(val displayName: String, val index: Int) {

    OPEN("Open", 0),
    CLOSED("Closed", 1);

    override fun toString(): String {
        return displayName
    }

    companion object {
        private val map = values().associateBy(PartyType::index)
        fun fromIndex(index: Int) = map[index]
    }
}