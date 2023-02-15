package presentation.popup

import Constants
import com.soywiz.klock.TimeSpan
import com.soywiz.korge.animate.animate
import com.soywiz.korge.animate.block
import com.soywiz.korge.animate.hide
import com.soywiz.korge.input.onClick
import com.soywiz.korge.view.*
import com.soywiz.korim.vector.format.SVG
import com.soywiz.korim.vector.render
import com.soywiz.korim.vector.scaled
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.Dispatchers
import presentation.StyledColors
import presentation.adapters.SizeAdapter

class PopupContainer(var content: View) : Container() {
    init {
        zIndex(200)
        solidRect(
            width = Constants.UI.WIDTH,
            height = Constants.UI.HEIGHT,
            color = StyledColors.theme.popupBlur,

        ) {
            onClick {
                hide()
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
                content.addTo(this)
                scaledHeight = content.height + SizeAdapter.marginXL
                launchImmediately(Dispatchers.Default) {
                    val svg = SVG(resourcesVfs["icons/close.svg"].readString())
                    svg.scaled(SizeAdapter.getScaleValueByAbsolute(svg.dwidth, SizeAdapter.cellSize / 1.5))
                    image(svg.render(true)) {
                        alignTopToTopOf(other = parent!!, padding = SizeAdapter.cellSize / 5)
                        alignRightToRightOf(other = parent!!, padding = SizeAdapter.cellSize / 5)
                        onClick { hide() }
                    }
                }
            }
        }
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
