package presentation.popup

import com.soywiz.korge.view.*
import presentation.*
import presentation.adapters.*

fun Container.outOfMovesPopup() {
    PopupContainer(OutOfMovesPopup()).addTo(this)
}

class OutOfMovesPopup : Container() {
    init {

        solidRect(100, 100,)

        text(
            text = "Out of Moves!",
            textSize = SizeAdapter.h2,
            font = DefaultFontFamily.font,
            // alignment = TextAlignment.CENTER
        ).centerOn(this)
    }
}
