package presentation.buttons

import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.*
import presentation.*
import presentation.adapters.*

/*
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

 */

class DiamondButton(
    diamondPrice: Int,
    bgColor: RGBA,
    imageResourcePath: String,
    imageWidth: Double,
    onClick: () -> Unit,
) : Container() {
    private var priceText = Text("")
    var diamondPrice: Int = diamondPrice; set(value) {
        if (field != value) {
            field = value
            priceText.text = value.toString()
        }
    }

    init {
        val imageBtn = imageButton(
            bgColor = bgColor,
            imageResourcePath = imageResourcePath,
            imageWidth = imageWidth,
            onClick = onClick
        )

        container {

            priceText = text(
                text = diamondPrice.toString(),
                textSize = SizeAdapter.h4,
                font = DefaultFontFamily.font
            ) {
                alignTopToBottomOf(imageBtn)
                centerXOn(imageBtn)
            }

            launchImmediately(Dispatchers.Default) {

                val svg = resourcesVfs["/icons/diamond.svg"].readSVG()
                val drawable = svg.scaled(
                    SizeAdapter.getScaleValueByAbsolute(
                        initialWidth = svg.width.toDouble(),
                        settingWidth = SizeAdapter.cellSize * .18
                    )
                )
                val diamondIcon = image(texture = drawable.render())
                    .alignRightToLeftOf(priceText, SizeAdapter.marginXXS)
                    .centerYOn(priceText)
            }
        }
    }
}
