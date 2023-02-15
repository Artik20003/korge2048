package presentation

import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import presentation.adapters.*

fun Container.button(text: String, callback: () -> Unit) =
    Button(
        text = text,
        callback = callback
    ).addTo(this)

class Button(
    val text: String,
    val callback: () -> Unit,
) : Container() {

    init {
        val cellSize = SizeAdapter.cellSize
        roundRect(
            width = cellSize * 2.5,
            height = cellSize / 2,
            rx = SizeAdapter.cellSize * .05,
            fill = Colors.TRANSPARENT_WHITE,
            stroke = Colors.WHITE,
            strokeThickness = SizeAdapter.borderStroke
        ) {
            onClick { callback() }
            text(
                text = text,
                textSize = SizeAdapter.h3,
                fill = Colors.WHITE,
                font = DefaultFontFamily.font
            ).apply {
                centerOn(this.parent!!)
            }
        }
    }
}
