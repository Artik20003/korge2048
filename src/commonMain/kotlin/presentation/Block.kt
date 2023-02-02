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

        roundRect(cellSize, cellSize, CellSizeAdapter.cellSize * .17, fill = PlayBlockColor.getColorByPower(power))
        text(
            text = BlockTextAdapter.getTextByPower(power),
            textSize = BlockTextAdapter.getFontSizeByPower(power),
            fill = Colors.WHITE,
            font = DefaultFontFamily.font
        ).apply {
            centerBetween(0.0, 0.0, cellSize, cellSize)
        }
    }
}
