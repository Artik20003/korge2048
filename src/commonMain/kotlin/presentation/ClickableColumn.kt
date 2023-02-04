package presentation

import Constants
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import presentation.adapters.*

fun Container.clickableColumn(onClick: () -> Unit) = ClickableColumn(onClick).addTo(this)

class ClickableColumn(onClick: () -> Unit) : Container() {

    init {
        solidRect(
            width = SizeAdapter.columnSize,
            height = SizeAdapter.columnSize * Constants.Playground.ROW_COUNT,
            color = RGBA.Companion.float(0.0, 0.0, 0.0, 0.0)
        ) {
            onClick {
                onClick()
            }
        }
    }
}
