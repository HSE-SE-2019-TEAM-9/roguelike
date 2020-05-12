package ru.hse.se.team9.model.mapgeneration.creators

import arrow.core.getOrHandle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.hse.se.team9.model.generators.positions.RandomPosition
import ru.hse.se.team9.util.DummyFileChooser
import ru.hse.se.team9.util.SimpleTestMap
import ru.hse.se.team9.util.getResourceFile
import java.io.File

internal class FromFileMapCreatorTest {

    @Test
    fun testCreateMap() {
        val deserializedMap = FromFileMapCreator.build(
            RandomPosition,
            DummyFileChooser(getResourceFile(this::class.java, "/serialized_map"))
        ).getOrHandle { throw it }.createMap().getOrHandle { throw it }
        val gameMap = SimpleTestMap.gameMap()

        assertEquals(gameMap.heroes, deserializedMap.heroes)
        assertEquals(gameMap.mobs.keys, deserializedMap.mobs.keys)
        assertEquals(gameMap.map, deserializedMap.map)
        assertEquals(gameMap.items, deserializedMap.items)
        assertEquals(gameMap.consumables, deserializedMap.consumables)
    }

    @Test
    fun testIncorrectFile() {
        assertTrue(FromFileMapCreator.build(
            RandomPosition,
            DummyFileChooser(getResourceFile(this::class.java, "/empty"))
        ).getOrHandle { throw it }.createMap().isLeft()
        )
    }
}
