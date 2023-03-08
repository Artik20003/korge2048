package presentation

import com.soywiz.klock.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.view.*
import com.soywiz.korim.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.interpolation.*
import kotlinx.coroutines.*
import presentation.adapters.*

fun Container.switchBlockContainer() =
    SwitchBlockContainer().addTo(this)

class SwitchBlockContainer() : Container() {

    init {
        launchImmediately(Dispatchers.Unconfined) {
            val spriteMap = resourcesVfs["sprites/light_circle.png"].readBitmap()
            val explosionAnimation = SpriteAnimation(
                spriteMap = spriteMap,
                spriteWidth = 300,
                spriteHeight = 300,
                marginTop = 0,
                marginLeft = 0,
                columns = 1,
                rows = 50,
                offsetBetweenColumns = 0,
                offsetBetweenRows = 0,
            )
            val leftContainer = container {
                sprite(explosionAnimation) { scale(0.8) }.playAnimationLooped()

                val switchingBlocksContainer = container {

                    val block1 = block(1, SizeAdapter.cellSize / 2)

                    val block2 = block(2, SizeAdapter.cellSize / 2)
                        .alignLeftToRightOf(block1)
                        .alignTopToBottomOf(block1)

                    val block1OriginalX = block1.x
                    val block1OriginalY = block1.y
                    val block2OriginalX = block2.x
                    val block2OriginalY = block2.y

                    val time = 1500.milliseconds
                    launchImmediately(Dispatchers.Unconfined) {
                        animate(looped = true) {
                            moveTo(
                                view = block1,
                                x = block2OriginalX,
                                y = block2OriginalY,
                                time = time,
                                easing = Easing.EASE_IN_ELASTIC
                            )
                            moveTo(
                                view = block1,
                                x = block1OriginalX,
                                y = block1OriginalY,
                                time = time,
                                easing = Easing.EASE_IN_ELASTIC
                            )
                        }
                    }
                    launchImmediately(Dispatchers.Unconfined) {
                        animate(looped = true) {
                            moveTo(
                                view = block2,
                                x = block1OriginalX,
                                y = block1OriginalY,
                                time = time,
                                easing = Easing.EASE_IN_ELASTIC
                            )
                            moveTo(
                                view = block2,
                                x = block2OriginalX,
                                y = block2OriginalY,
                                time = time,
                                easing = Easing.EASE_IN_ELASTIC
                            )
                        }
                    }
                }.centerOn(this.parent!!)
            }
            val rightContainer = container {
                val title = text(
                    text = "Swap",
                    textSize = SizeAdapter.h1,
                    font = DefaultFontFamily.font,
                )
                val description = text(
                    text = "Select block to swap",
                    textSize = SizeAdapter.h3,
                    font = DefaultFontFamily.font,
                ).alignTopToBottomOf(title, -(SizeAdapter.marginM))
            }.alignLeftToRightOf(leftContainer, -SizeAdapter.marginM).centerYOn(leftContainer)
        }
    }
}
