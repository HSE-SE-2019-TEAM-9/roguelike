package ru.hse.se.team9.consoleview

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextCharacter
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.input.KeyStroke
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.screen.Screen
import ru.hse.se.team9.entities.EmptySpace
import ru.hse.se.team9.entities.MapObject
import ru.hse.se.team9.entities.MobType.*
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.entities.views.MobView
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.view.KeyPressedType
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*

internal class MapComponent(
    private val map: MapView,
    private val screen: Screen,
    private val actionQueue: Queue<() -> Unit>,
    var keyPressedHandler: (KeyPressedType) -> Unit
) : AbstractInteractableComponent<MapComponent>() {

    override fun createDefaultRenderer(): InteractableRenderer<MapComponent>? {
        return MapRenderer()
    }

    override fun handleKeyStroke(keyStroke: KeyStroke): Interactable.Result {
        val key = when (keyStroke.keyType) {
            KeyType.ArrowUp -> KeyPressedType.UP
            KeyType.ArrowDown -> KeyPressedType.DOWN
            KeyType.ArrowLeft -> KeyPressedType.LEFT
            KeyType.ArrowRight -> KeyPressedType.RIGHT
            KeyType.Escape -> KeyPressedType.ESCAPE
            else -> null
        }
        if (key != null) {
            actionQueue.add {
                keyPressedHandler(key)
            }
        }
        return Interactable.Result.HANDLED
    }

    private class MapRenderer : InteractableRenderer<MapComponent> {
        override fun getPreferredSize(component: MapComponent): TerminalSize {
            return TerminalSize.ONE
        }

        override fun drawComponent(graphics: TextGUIGraphics, component: MapComponent) {
            graphics.backgroundColor = BACKGROUND_COLOR
            graphics.fill(NOTHING_CHARACTER)

            val heroPosition = component.map.hero.position
            val gameMap = component.map.map
            val mobs = component.map.mobs

            val screenSize = component.screen.terminalSize
            val (xLeft, xRight) = getBounds(heroPosition.x, screenSize.columns, gameMap[0].size)
            val (yHigh, yLow) = getBounds(heroPosition.y, screenSize.rows, gameMap.size)
            drawMap(xLeft, xRight, yHigh, yLow, gameMap, graphics)
            drawMobs(xLeft, yHigh, mobs, graphics)
            drawHero(xLeft, yHigh, heroPosition, graphics)
        }

        private fun drawMobs(xLeft: Int, yHigh: Int, mobs: List<MobView>, graphics: TextGUIGraphics) {
            for (mob in mobs) {
                val color = when (mob.type) {
                    BIG -> BIG_MOB_COLOR
                    MEDIUM -> MEDIUM_MOB_COLOR
                    SMALL -> SMALL_MOB_COLOR
                }
                graphics.setCharacter(
                    mob.position.x - xLeft, mob.position.y - yHigh,
                    TextCharacter(MOB_CHARACTER, color, BACKGROUND_COLOR)
                )
            }
        }

        private fun drawHero(
            xLeft: Int,
            yHigh: Int,
            heroPosition: Position,
            graphics: TextGUIGraphics
        ) {
            graphics.setCharacter(
                heroPosition.x - xLeft, heroPosition.y - yHigh,
                TextCharacter(HERO_CHARACTER, HERO_COLOR, BACKGROUND_COLOR)
            )
        }

        private fun drawMap(
            xLeft: Int,
            xRight: Int,
            yHigh: Int,
            yLow: Int,
            gameMap: List<List<MapObject>>,
            graphics: TextGUIGraphics
        ) {
            for (x in xLeft until xRight) {
                for (y in yHigh until yLow) {
                    val character = when (gameMap[y][x]) {
                        Wall -> TextCharacter(WALL_CHARACTER, WALL_COLOR, BACKGROUND_COLOR)
                        EmptySpace -> TextCharacter(EMPTY_SPACE_CHARACTER, EMPTY_SPACE_COLOR, BACKGROUND_COLOR)
                    }
                    graphics.setCharacter(x - xLeft, y - yHigh, character)
                }
            }
        }

        override fun getCursorLocation(component: MapComponent?): TerminalPosition {
            return TerminalPosition(-1, -1)
        }
    }

    companion object {
        private val BACKGROUND_COLOR = TextColor.ANSI.BLACK
        private val HERO_COLOR = TextColor.RGB(255, 255, 0)
        private val EMPTY_SPACE_COLOR = TextColor.ANSI.YELLOW
        private val WALL_COLOR = TextColor.RGB(0, 100, 0)
        private val BIG_MOB_COLOR = TextColor.RGB(255, 0, 0)
        private val MEDIUM_MOB_COLOR = TextColor.RGB(255, 0, 255)
        private val SMALL_MOB_COLOR = TextColor.RGB(0, 0, 255)

        private const val HERO_CHARACTER = '@'
        private const val EMPTY_SPACE_CHARACTER = '.'
        private const val WALL_CHARACTER = '#'
        private const val NOTHING_CHARACTER = ' '
        private const val MOB_CHARACTER = 'U'

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