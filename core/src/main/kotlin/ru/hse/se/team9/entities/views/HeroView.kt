package ru.hse.se.team9.entities.views

import ru.hse.se.team9.positions.Position

/** A part of hero which is allowed to be shown by View. */
interface HeroView {
    /** Position of hero on the map */
    val position: Position
    /** Health of hero */
    val hp: Int
    /** Maximum health of hero */
    val maxHp: Int
    /** Armor of hero */
    val armor: Int
    /** Damage of hero */
    val damage: Int
    /** All collected items, excluding equipped */
    val inventory: List<ItemView>
    /** All equipped items */
    val equipment: List<ItemView>
}