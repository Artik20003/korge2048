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
                callback = { popupContainer!!.hide() }
            ).centerXOn(parentContainer).alignTopToBottomOf(text2, SizeAdapter.marginL)

            button(
                text = "YES",
                type = Button.ButtonType.DISAGREE,
                callback = {}
            ).centerXOn(parentContainer).alignTopToBottomOf(noBtn, SizeAdapter.marginS)
        }
    }
}
