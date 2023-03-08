package presentation

import com.soywiz.klock.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.interpolation.*
import domain.playground.*
import kotlinx.coroutines.*
import presentation.adapters.*

fun Container.block(power: Int, cellSize: Double, isHighest: Boolean = false) =
    Block(
        power = power,
        cellSize = cellSize,
        isHighest = isHighest
    ).addTo(this)

class Block(
    val power: Int,
    val cellSize: Double,
    var isHighest: Boolean = false,
    var isSelected: Boolean = false

) : Container() {
    var block: Container = container { }

    init {
        redrawBlock()
    }

    fun redrawBlock() {
        val oldBlock = block
        block = fixedSizeContainer(cellSize, cellSize, true) {

            roundRect(
                width = cellSize,
                height = cellSize,
                rx = SizeAdapter.cellSize * .17,
                fill = PlayBlockColor.getColorByPower(power),
            )
            text(
                text = BlockTextAdapter.getTextByPower(power),
                textSize = BlockTextAdapter.getFontSizeByPower(power),
                fill = Colors.WHITE,
                font = DefaultFontFamily.font,

            ).apply {
                centerBetween(0.0, 0.0, cellSize, cellSize)
            }
            val selectionStroke = SizeAdapter.cellSize * .07
            if (isSelected) {
                roundRect(
                    width = cellSize - selectionStroke / 2,
                    height = cellSize - selectionStroke / 2,
                    rx = SizeAdapter.cellSize * .17,
                    fill = Colors.TRANSPARENT_WHITE,
                    strokeThickness = selectionStroke
                )
            }

            if (isHighest) {

                launchImmediately(Dispatchers.Default) {

                    val starPos = listOf<StarProperties>(
                        // left top
                        StarProperties(
                            relativeX = .15,
                            relativeY = .15,
                            scale = .2,
                            rotateDegree = 20,
                        ),

                        // left bottom
                        StarProperties(
                            relativeX = .15,
                            relativeY = .75,
                            scale = .1,
                            rotateDegree = 20,
                        ),

                        StarProperties(
                            relativeX = .25,
                            relativeY = .7,
                            scale = .15,
                            rotateDegree = 45,
                        ),
                        StarProperties(
                            relativeX = .12,
                            relativeY = .65,
                            scale = .1,
                            rotateDegree = 80,
                        ),
                        // right bottom
                        StarProperties(
                            relativeX = .75,
                            relativeY = .75,
                            scale = .2,
                            rotateDegree = 30,
                        ),
                        // right top
                        StarProperties(
                            relativeX = .7,
                            relativeY = .16,
                            scale = .15,
                            rotateDegree = 45,
                        ),
                        StarProperties(
                            relativeX = .85,
                            relativeY = .2,
                            scale = .1,
                            rotateDegree = 80,
                        ),
                    )
                    val starSvg = resourcesVfs["icons/star.svg"].readSVG()
                    starPos.forEach { starProperties ->
                        val restartDrawable = starSvg.scaled(
                            SizeAdapter.getScaleValueByAbsolute(
                                initialWidth = starSvg.width.toDouble(),
                                settingWidth = SizeAdapter.cellSize * starProperties.scale
                            )
                        )
                        val image = image(texture = restartDrawable.render()) {
                            alpha = .0
                            rotation = Angle.fromDegrees(starProperties.rotateDegree)
                            anchor(.5, .5)
                            setPositionRelativeTo(
                                view = this.parent!!,
                                pos = Point(
                                    x = this.parent!!.width * starProperties.relativeX,
                                    y = this.parent!!.height * starProperties.relativeY
                                )
                            )
                        }
                        launchImmediately(Dispatchers.Unconfined) {
                            while (true) {
                                image.tween(
                                    image::scale[1],
                                    image::alpha[starProperties.opacity],
                                    time = 2.seconds,
                                    easing = Easing.EASE_IN
                                )
                                delay((0..2000L).random())
                                image.tween(
                                    image::scale[0.0], image::alpha[0],
                                    time = 2.seconds,
                                    easing = Easing.EASE_IN,

                                )
                            }
                        }
                    }
                }
            }
        }

        oldBlock.removeFromParent()
    }
}

class StarProperties(
    val relativeX: Double,
    val relativeY: Double,
    val scale: Double = .2,
    val opacity: Double = .7,
    val rotateDegree: Int = 0,

)
