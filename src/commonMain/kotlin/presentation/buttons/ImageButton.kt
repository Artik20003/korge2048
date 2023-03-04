package presentation.buttons

import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.*
import presentation.adapters.*

fun Container.imageButton(
    bgColor: RGBA,
    imageResourcePath: String,
    imageWidth: Double,
    onClick: () -> Unit,
) = ImageButton(
    bgColor = bgColor,
    imageResourcePath = imageResourcePath,
    imageWidth = imageWidth,
    onClick = onClick,
).addTo(this)

class ImageButton(
    bgColor: RGBA,
    imageResourcePath: String,
    imageWidth: Double,
    onClick: () -> Unit,
) : Container() {

    init {
        roundRect(
            width = SizeAdapter.cellSize,
            height = SizeAdapter.cellSize,
            rx = SizeAdapter.cellSize * .17,
            strokeThickness = SizeAdapter.borderStroke,
            fill = bgColor
        ) {
            launchImmediately(Dispatchers.Default) {
                val restartSvg = resourcesVfs[imageResourcePath].readSVG()
                val restartDrawable = restartSvg.scaled(
                    SizeAdapter.getScaleValueByAbsolute(
                        initialWidth = restartSvg.width.toDouble(),
                        settingWidth = imageWidth
                    )
                )

                image(texture = restartDrawable.render()) {
                    centerOn(this.parent!!)

                    onClick {
                        onClick()
                    }
                }
            }
        }
    }
}
