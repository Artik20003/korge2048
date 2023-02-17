package presentation.popup

import Constants
import com.soywiz.klock.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.*
import presentation.*
import presentation.adapters.*

class PopupContainer(
    var content: PopupContent,
    var onClose: () -> Unit = {},
) : PopupContent() {
    init {
        val popupContainer = this
        zIndex(200)
        solidRect(
            width = Constants.UI.WIDTH,
            height = Constants.UI.HEIGHT,
            color = StyledColors.theme.popupBlur,

        ) {
            onClick {
                close()
            }
            roundRect(
                width = SizeAdapter.cellSize * Constants.Playground.COL_COUNT,
                height = SizeAdapter.cellSize * Constants.Playground.ROW_COUNT,
                rx = SizeAdapter.cellSize * 0.17,
                fill = StyledColors.theme.popupBg,
                strokeThickness = 5.0,
                stroke = StyledColors.theme.popupStroke
            ) {
                // empty onClick to prevent event handling from parent
                onClick { }
                centerOn(this.parent ?: containerRoot)
                content.popupContainer = popupContainer
                content.addTo(this)
                scaledHeight = content.height + SizeAdapter.marginXL
                launchImmediately(Dispatchers.Default) {
                    val svg = SVG(resourcesVfs["icons/close.svg"].readString())
                    svg.scaled(SizeAdapter.getScaleValueByAbsolute(svg.dwidth, SizeAdapter.cellSize / 1.5))
                    image(svg.render(true)) {
                        alignTopToTopOf(other = parent!!, padding = SizeAdapter.cellSize / 5)
                        alignRightToRightOf(other = parent!!, padding = SizeAdapter.cellSize / 5)
                        onClick { close() }
                    }
                }
            }
        }
    }

    fun close() {
        hide()
        onClose()
    }

    fun hide() {
        val container = this
        launchImmediately(Dispatchers.Default) {
            this.animate {
                parallel {
                    this.hide(
                        view = container,
                        time = TimeSpan(500.0)
                    )
                }
                block {
                    removeFromParent()
                }
            }
        }
    }
}
