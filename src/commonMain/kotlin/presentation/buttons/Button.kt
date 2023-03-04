package presentation.buttons

import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import presentation.*
import presentation.adapters.*

fun Container.button(text: String, type: Button.ButtonType = Button.ButtonType.DEFAULT, callback: () -> Unit) =
    Button(
        text = text,
        type = type,
        callback = callback
    ).addTo(this)

class Button(
    val text: String,
    val callback: () -> Unit,
    val type: ButtonType = ButtonType.DEFAULT
) : Container() {

    init {

        val fill = when (type) {
            ButtonType.DEFAULT -> Colors.TRANSPARENT_WHITE
            ButtonType.AGREE -> Colors["#1d7656"]
            ButtonType.DISAGREE -> Colors["#ea5165"]
        }

        val cellSize = SizeAdapter.cellSize
        roundRect(
            width = cellSize * 2.5,
            height = cellSize / 2,
            rx = SizeAdapter.cellSize * .05,
            fill = fill,
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

    enum class ButtonType {
        DEFAULT,
        AGREE,
        DISAGREE,
    }
}
