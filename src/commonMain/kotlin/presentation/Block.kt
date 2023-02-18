package presentation

import com.soywiz.klock.seconds
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.format.readSVG
import com.soywiz.korim.vector.render
import com.soywiz.korim.vector.scaled
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.Point
import com.soywiz.korma.interpolation.Easing
import domain.playground.PlayBlockColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import presentation.adapters.BlockTextAdapter
import presentation.adapters.SizeAdapter

fun Container.block(power: Int, cellSize: Double, isHighest: Boolean = false) =
    Block(
        power = power,
        cellSize = cellSize,
        isHighest = isHighest
    ).addTo(this)

class Block(
    val power: Int,
    val cellSize: Double,
    val isHighest: Boolean = false,
) : Container() {

    init {

        roundRect(cellSize, cellSize, SizeAdapter.cellSize * .17, fill = PlayBlockColor.getColorByPower(power))
        text(
            text = BlockTextAdapter.getTextByPower(power),
            textSize = BlockTextAdapter.getFontSizeByPower(power),
            fill = Colors.WHITE,
            font = DefaultFontFamily.font
        ).apply {
            centerBetween(0.0, 0.0, cellSize, cellSize)
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
                    println("vendeta")
                }
            }
        }
    }
}

class StarProperties(
    val relativeX: Double,
    val relativeY: Double,
    val scale: Double = .2,
    val opacity: Double = .7,
    val rotateDegree: Int = 0,

)
