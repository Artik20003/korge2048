package presentation.popup.content

import Event
import com.soywiz.korge.bus.*
import com.soywiz.korge.view.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.*
import presentation.*
import presentation.adapters.*
import presentation.buttons.button
import presentation.popup.*

fun Container.outOfMovesPopup(bus: GlobalBus) {
    PopupContainer(
        content = OutOfMovesPopup(bus = bus),
        onClose = {
            launchImmediately(Dispatchers.Default) {
                bus.send(Event.GameOver)
            }
        }
    ).addTo(containerRoot)
}

class OutOfMovesPopup(val bus: GlobalBus) : PopupContent() {

    override fun onParentChanged() {
        println("popupContainer")
        println(popupContainer)
        if (popupContainer == null) return
        super.onParentChanged()

        this.parent?.let { parentContainer ->

            val h1 = text(
                text = "Out of Moves!",
                textSize = SizeAdapter.h2,
                font = DefaultFontFamily.font,
            ) {
                alignTopToTopOf(parentContainer, SizeAdapter.marginM)
                centerXOn(parentContainer)
            }

            val h2 = text(
                text = "Keep playing?",
                textSize = SizeAdapter.h3,
                font = DefaultFontFamily.font,
            ).centerXOn(parentContainer).alignTopToBottomOf(h1)

            val roundRect = roundRect(
                width = SizeAdapter.cellSize,
                height = SizeAdapter.cellSize,
                rx = SizeAdapter.cornerRadius,
                fill = StyledColors.theme.mainBg,
                strokeThickness = SizeAdapter.borderStroke

            ) {
                centerXOn(parentContainer)
                alignTopToBottomOf(h2, SizeAdapter.marginL)
                launchImmediately(Dispatchers.Default) {
                    val svg = resourcesVfs["/icons/crown.svg"].readSVG()
                    image(texture = svg.render(false)) {
                        scale = SizeAdapter.getScaleValueByAbsolute(svg.dwidth, SizeAdapter.cellSize)
                        centerOn(this.parent!!)
                    }
                }
            }
            button(
                text = "No Thanks",
                callback = {
                    popupContainer!!.hide()
                    restartPopup(bus)
                }
            )
                .alignTopToBottomOf(this.parent!!)
                .centerXOn(roundRect)
                .alignTopToBottomOf(roundRect, SizeAdapter.marginXL)
        }
    }
}
