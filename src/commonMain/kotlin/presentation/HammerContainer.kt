package presentation

import com.soywiz.klock.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.view.*
import com.soywiz.korim.format.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.interpolation.*
import kotlinx.coroutines.*
import presentation.adapters.*

fun Container.hammerContainer() =
    HammerContainer().addTo(this)

class HammerContainer() : Container() {

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
                val hammerSvg = resourcesVfs["icons/hammer.svg"].readSVG()
                val hammerDrawable = hammerSvg.scaled(
                    SizeAdapter.getScaleValueByAbsolute(
                        initialWidth = hammerSvg.width.toDouble(),
                        settingWidth = SizeAdapter.cellSize
                    )
                )

                val hammer = image(texture = hammerDrawable.render()) {
                    centerOn(this.parent!!)
                }
                launchImmediately(Dispatchers.Unconfined) {
                    animate(looped = true) {

                        rotateTo(
                            view = hammer,
                            rotation = { Angle.fromDegrees(45.0) },
                            time = 2000.milliseconds,
                            easing = Easing.EASE_IN_ELASTIC

                        )
                        rotateTo(
                            view = hammer,
                            rotation = { Angle.fromDegrees(0.0) },
                            time = 1000.milliseconds,
                        )
                    }
                }
            }
            val rightContainer = container {
                val title = text(
                    text = "Smash!",
                    textSize = SizeAdapter.h1,
                    font = DefaultFontFamily.font,
                )
                val description = text(
                    text = "Select block to remove",
                    textSize = SizeAdapter.h3,
                    font = DefaultFontFamily.font,
                ).alignTopToBottomOf(title, -(SizeAdapter.marginM))
            }.alignLeftToRightOf(leftContainer, -SizeAdapter.marginM).centerYOn(leftContainer)
        }
    }
}
