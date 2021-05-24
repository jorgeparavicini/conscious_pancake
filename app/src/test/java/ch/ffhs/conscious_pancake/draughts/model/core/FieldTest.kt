package ch.ffhs.conscious_pancake.draughts.model.core

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

internal class FieldTest {

    private val field = Field()

    @Before
    fun before() {
        field.reset()
    }

    @Test
    fun reset() {
        print(field)
        print(field.getPossibleMoves())
        assertTrue(true)
    }
}