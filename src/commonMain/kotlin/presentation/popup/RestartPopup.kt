package presentation.popup

import com.soywiz.korge.view.*
import presentation.*
import presentation.adapters.*

fun Container.restartPopup() {
    PopupContainer(RestartPopup()).addTo(containerRoot)
}

class RestartPopup() : PopupContent() {

    override fun onParentChanged() {
        if (popupContainer == null) return
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

            text(
                text = "This will reset your current game and score to zero",
                textSize = SizeAdapter.h3,
                font = DefaultFontFamily.font,
            ).centerXOn(parentContainer).alignTopToBottomOf(h1)
        }
    }
}
