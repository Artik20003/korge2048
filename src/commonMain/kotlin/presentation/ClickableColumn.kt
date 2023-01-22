package presentation

import Constants
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import domain.*
import kotlin.math.*

fun Container.clickableColumn(onClick: () -> Unit) = ClickableColumn(onClick).addTo(this)

class ClickableColumn(onClick: () -> Unit) : Container() {
    val cellSize: Double = 50.0
    init {
        solidRect(
            width = cellSize,
            height = cellSize * Constants.Playground.ROW_COUNT,
            color = RGBA.Companion.float(0.0,0.0,0.0,0)
        ) {
            onClick {
                onClick()
            }
        }
    }
}


