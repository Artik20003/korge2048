package presentation.buttons

import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.*
import presentation.adapters.*

/*
fun Container.backButton() =
    HammerContainer().addTo(this)
*/
class BackButton() : Container() {

    init {
        visible(false)
        launchImmediately(Dispatchers.Default) {

            val hammerSvg = resourcesVfs["icons/back-arrow.svg"].readSVG()
            val hammerDrawable = hammerSvg.scaled(
                SizeAdapter.getScaleValueByAbsolute(
                    initialWidth = hammerSvg.width.toDouble(),
                    settingWidth = SizeAdapter.cellSize / 2
                )
            )

            image(texture = hammerDrawable.render()) {
                centerOn(this.parent!!)
            }
        }
    }

    fun activate(callback: () -> Unit) {
        visible(true)
        mouseEnabled = true
        onClick {
            callback()
            mouse.click.clear()
            mouseEnabled = false
            visible(false)
        }
    }
}
