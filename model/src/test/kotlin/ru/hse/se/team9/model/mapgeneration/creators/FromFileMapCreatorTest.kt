package ru.hse.se.team9.model.mapgeneration.creators

import arrow.core.getOrHandle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.hse.se.team9.model.random.positions.RandomPosition
import ru.hse.se.team9.util.DummyFileChooser
import ru.hse.se.team9.util.SimpleTestMap
import ru.hse.se.team9.util.getResourceFile

internal class FromFileMapCreatorTest {

    //TODO: fixme
//    @Test
//    fun testCreateMap() {
//        val map = FromFileMapCreator.build(
//            RandomPosition,
//            DummyFileChooser(getResourceFile(this::class.java, "/serializedMap.txt"))
//        ).getOrHandle { throw it }.createMap().getOrHandle { throw it }
//        assertEquals(SimpleTestMap.gameMap, map)
//    }
//
//    @Test
//    fun testIncorrectFile() {
//        assertTrue(FromFileMapCreator.build(
//            RandomPosition,
//            DummyFileChooser(getResourceFile(this::class.java, "/empty.txt"))
//        ).getOrHandle { throw it }.createMap().isLeft()
//        )
//    }
}
