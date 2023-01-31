package presentation

import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import domain.playground.*
import kotlin.math.*

fun Container.block(power: Int, cellSize: Double) =
    Block(
        power = power,
        cellSize = cellSize
    ).addTo(this)

class Block(
    val power: Int,
    val cellSize: Double
) : Container() {

    init {
        roundRect(cellSize, cellSize, 5.0, fill = PlayBlockColor.getColorByPower(power))
        text(2.0.pow(power).toLong().toString(), 25.0, Colors.WHITE).apply {
            centerBetween(0.0, 0.0, cellSize, cellSize)
        }
    }


}


