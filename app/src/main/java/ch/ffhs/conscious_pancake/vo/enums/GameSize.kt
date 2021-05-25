package ch.ffhs.conscious_pancake.vo.enums

import com.jorgeparavicini.draughts.model.enums.FieldSize

enum class GameSize(val displayName: String, val index: Int, val fieldSize: FieldSize) {

    SIZE_8X8("8x8", 0, FieldSize.SMALL),
    SIZE_6X6("10x10", 1, FieldSize.LARGE);

    override fun toString(): String {
        return displayName
    }

    companion object {
        private val map = values().associateBy(GameSize::index)
        fun fromIndex(index: Int) = map[index]
    }
}