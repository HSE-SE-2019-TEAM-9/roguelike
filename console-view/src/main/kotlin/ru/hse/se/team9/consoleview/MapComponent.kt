package ru.hse.se.team9.consoleview

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.AbstractComponent
import com.googlecode.lanterna.gui2.ComponentRenderer
import com.googlecode.lanterna.gui2.TextGUIGraphics
import ru.hse.se.team9.entities.EmptySpace
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.positions.Position
import java.lang.Integer.max
import java.lang.Integer.min

internal class MapComponent(
    private val map: List<List<MapObject>>,
    private val width: Int,
    private val height: Int,
    private val heroPosition: Position) : AbstractComponent<MapComponent>() {

    override fun createDefaultRenderer(): ComponentRenderer<MapComponent> {
        return MapRenderer()
    }

    private class MapRenderer : ComponentRenderer<MapComponent> {
        override fun getPreferredSize(component: MapComponent): TerminalSize {
            return TerminalSize(component.width, component.height)
        }

        override fun drawComponent(graphics: TextGUIGraphics, component: MapComponent) {
            graphics.backgroundColor = BACKGROUND_COLOR
            graphics.fill(NOTHING_CHARACTER)
            val (xLeft, xRight) = getBounds(component.heroPosition.x, component.width, component.map[0].size)
            val (yHigh, yLow) = getBounds(component.heroPosition.y, component.height, component.map.size)
            for (x in xLeft until xRight) {
                for (y in yHigh until yLow) {
                    val character = when (component.map[y][x]) {
                        Wall -> TextCharacter(WALL_CHARACTER, WALL_COLOR, BACKGROUND_COLOR)
                        EmptySpace -> TextCharacter(EMPTY_SPACE_CHARACTER, EMPTY_SPACE_COLOR, BACKGROUND_COLOR)
                    }
                    graphics.setCharacter(x - xLeft, y - yHigh, character)
                }
            }
            graphics.setCharacter(
                component.heroPosition.x - xLeft, component.heroPosition.y - yHigh,
                TextCharacter(HERO_CHARACTER, HERO_COLOR, BACKGROUND_COLOR)
            )
        }
    }

    companion object {
        private val BACKGROUND_COLOR = TextColor.ANSI.BLACK
        private val HERO_COLOR = TextColor.ANSI.RED
        private val EMPTY_SPACE_COLOR = TextColor.ANSI.YELLOW
        private val WALL_COLOR = TextColor.ANSI.GREEN

        private const val HERO_CHARACTER = '@'
        private const val EMPTY_SPACE_CHARACTER = '.'
        private const val WALL_CHARACTER = '#'
        private const val NOTHING_CHARACTER = ' '

        private fun getBounds(position: Int, width: Int, maxWidth: Int): Pair<Int, Int> {
            val minBound = position - (width - 1) / 2
            val maxBound = position + (width + 2) / 2
            return when {
                minBound < 0 -> Pair(0, min(width, maxWidth))
                maxBound > maxWidth -> Pair(max(maxWidth - width, 0), maxWidth)
                else -> Pair(minBound, maxBound)
            }
        }
    }
}