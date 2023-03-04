package presentation.popup.content

import Event
import com.soywiz.korge.bus.*
import com.soywiz.korge.view.*
import com.soywiz.korio.async.*
import kotlinx.coroutines.*
import presentation.*
import presentation.adapters.*
import presentation.buttons.Button
import presentation.buttons.button
import presentation.popup.*

fun Container.restartPopup(bus: GlobalBus) {
    PopupContainer(
        content = RestartPopup(bus = bus),
        onClose = {
            launchImmediately(Dispatchers.Default) {
                bus.send(Event.GameOver)
            }
        }
    ).addTo(containerRoot)
}

class RestartPopup(val bus: GlobalBus) : PopupContent() {

    override fun onParentChanged() {
        val popupContainer = this.popupContainer ?: return
        super.onParentChanged()

        this.parent?.let { parentContainer ->

            val h1 = text(
                text = "Are you sure?",
                textSize = SizeAdapter.h2,
                font = DefaultFontFamily.font,

            ) {
                alignTopToTopOf(parentContainer, SizeAdapter.marginM)
                centerXOn(parentContainer)
            }

            val text1 = text(
                text = "This will reset your current",
                textSize = SizeAdapter.h4,
                font = DefaultFontFamily.font,
            ).centerXOn(parentContainer).alignTopToBottomOf(h1, SizeAdapter.marginM)

            val text2 = text(
                text = "game and score to zero",
                textSize = SizeAdapter.h4,
                font = DefaultFontFamily.font,

            ).centerXOn(parentContainer).alignTopToBottomOf(text1,)

            val noBtn = button(
                text = "NO",
                type = Button.ButtonType.AGREE,
                callback = {
                    popupContainer.hide()
                    outOfMovesPopup(bus)
                }
            ).centerXOn(parentContainer).alignTopToBottomOf(text2, SizeAdapter.marginL)

            button(
                text = "YES",
                type = Button.ButtonType.DISAGREE,
                callback = {
                    launchImmediately(Dispatchers.Default) {
                        popupContainer.hide()
                        bus.send(Event.GameOver)
                    }
                }
            ).centerXOn(parentContainer).alignTopToBottomOf(noBtn, SizeAdapter.marginS)
        }
    }
}
