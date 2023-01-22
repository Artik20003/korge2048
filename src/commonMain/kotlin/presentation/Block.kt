package presentation

import Constants
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import domain.*
import kotlin.math.*

fun Container.block(power: Int) =
    Block(
        power = power
    ).addTo(this)

class Block(
    val power: Int
) : Container() {
    val cellSize: Double = 50.0

    init {
        roundRect(cellSize, cellSize, 5.0, fill = PlayBlockColor.getColorByPower(power))
        text(2.0.pow(power).toLong().toString(), 25.0, Colors.WHITE).apply {
            centerBetween(0.0, 0.0, cellSize, cellSize)
        }
    }


}


