package presentation

import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import domain.playground.*
import presentation.adapters.*

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
        text(BlockTextAdapter.getTextByPower(power), BlockTextAdapter.getFontSizeByPower(power), Colors.WHITE).apply {
            centerBetween(0.0, 0.0, cellSize, cellSize)
        }
    }


}


