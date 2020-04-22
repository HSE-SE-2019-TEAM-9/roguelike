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
import ru.hse.se.team9.entities.MobProperty
import ru.hse.se.team9.entities.Wall
import ru.hse.se.team9.entities.views.MapView
import ru.hse.se.team9.entities.views.MobView
import ru.hse.se.team9.positions.Position
import ru.hse.se.team9.view.KeyPressedType
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*
import kotlin.math.roundToInt

/** Represents a screen with a game map. */
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
            return TerminalSize(Int.MAX_VALUE, Int.MAX_VALUE)
        }

        override fun drawComponent(graphics: TextGUIGraphics, component: MapComponent) {
            graphics.backgroundColor = BACKGROUND_COLOR
            graphics.fill(NOTHING_CHARACTER)

            val heroPosition = component.map.hero.position
            val gameMap = component.map.map
            val mobs = component.map.mobs
            val fog = component.map.fog

            val screenSize = component.screen.terminalSize
            val (xLeft, xRight) = getBounds(heroPosition.x, screenSize.columns, component.map.width)
            val (yHigh, yLow) = getBounds(heroPosition.y, screenSize.rows, component.map.height)
            drawMap(xLeft, xRight, yHigh, yLow, gameMap, graphics)
            drawMobs(xLeft, yHigh, mobs, graphics)
            drawHero(xLeft, yHigh, heroPosition, graphics)
            drawFog(xLeft, xRight, yHigh, yLow, fog, graphics)
        }

        private fun drawFog(
            xLeft: Int,
            xRight: Int,
            yHigh: Int,
            yLow: Int,
            fog: List<List<Boolean>>,
            graphics: TextGUIGraphics
        ) {
            for (x in xLeft until xRight) {
                for (y in yHigh until yLow) {
                    if (fog.getOrNull(y)?.getOrNull(x) != false) {
                        graphics.setCharacter(
                            x - xLeft,
                            y - yHigh,
                            TextCharacter(HIDDEN_CHARACTER, HIDDEN_COLOR, HIDDEN_BACKGROUND_COLOR)
                        )
                    }
                }
            }
        }

        private fun drawMobs(xLeft: Int, yHigh: Int, mobs: List<MobView>, graphics: TextGUIGraphics) {
            for (mob in mobs) {
                val color = getMobColor(mob.hp, mob.maxHp)
                val character =
                    if (mob.properties.contains(MobProperty.CONFUSED)) CONFUSED_MOB_CHARACTER else MOB_CHARACTER
                graphics.setCharacter(
                    mob.position.x - xLeft, mob.position.y - yHigh,
                    TextCharacter(character, color, BACKGROUND_COLOR)
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
                    val character = when (gameMap.getOrNull(y)?.getOrNull(x) ?: Wall) {
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
        private val HERO_COLOR = TextColor.Indexed.fromRGB(255, 255, 0)
        private val EMPTY_SPACE_COLOR = TextColor.ANSI.YELLOW
        private val FULL_HP_MOB_COLOR = TextColor.Indexed.fromRGB(190, 255, 0)
        private val NO_HP_MOB_COLOR = TextColor.Indexed.fromRGB(255, 0, 0)
        private val WALL_COLOR = TextColor.Indexed.fromRGB(0, 100, 0)
        private val HIDDEN_BACKGROUND_COLOR = TextColor.ANSI.BLACK
        private val HIDDEN_COLOR = TextColor.ANSI.BLACK

        private const val HERO_CHARACTER = '@'
        private const val EMPTY_SPACE_CHARACTER = '.'
        private const val WALL_CHARACTER = '#'
        private const val NOTHING_CHARACTER = ' '
        private const val MOB_CHARACTER = 'U'
        private const val HIDDEN_CHARACTER = '?'
        private const val CONFUSED_MOB_CHARACTER = '?'

        private fun getBounds(position: Int, width: Int, maxWidth: Int): Pair<Int, Int> {
            val minBound = position - (width - 1) / 2
            val maxBound = position + (width + 2) / 2
            return Pair(minBound, maxBound)
        }

        private fun normalizeRgbColor(component: Double): Int = max(0, min(255, component.roundToInt()))

        private fun getMobColor(hp: Int, fullHp: Int): TextColor.Indexed {
            val hpFraction = hp * 1.0 / fullHp
            val red = NO_HP_MOB_COLOR.toColor().red +
                    (FULL_HP_MOB_COLOR.toColor().red - NO_HP_MOB_COLOR.toColor().red) * hpFraction
            val green = NO_HP_MOB_COLOR.toColor().green +
                    (FULL_HP_MOB_COLOR.toColor().green - NO_HP_MOB_COLOR.toColor().green) * hpFraction
            val blue = NO_HP_MOB_COLOR.toColor().blue +
                    (FULL_HP_MOB_COLOR.toColor().blue - NO_HP_MOB_COLOR.toColor().blue) * hpFraction

            return TextColor.Indexed.fromRGB(normalizeRgbColor(red), normalizeRgbColor(green), normalizeRgbColor(blue))
        }
    }
}