package presentation.topbar

import com.soywiz.korge.view.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import domain.diamond.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import presentation.adapters.*

class DiamondCount(
    val diamondManager: DiamondManager,
    val alignmentContainer: Container
) : Container() {
    init {
        launchImmediately(Dispatchers.Unconfined) {
            container {
                val diamondContainer = this

                val svg = resourcesVfs["/icons/diamond.svg"].readSVG()
                val drawable = svg.scaled(
                    SizeAdapter.getScaleValueByAbsolute(
                        initialWidth = svg.width.toDouble(),
                        settingWidth = SizeAdapter.cellSize * 0.25
                    )
                )

                val diamondIcon = image(texture = drawable.render()).centerYOn(diamondContainer)

                text(
                    text = diamondManager.state.value.count.toString(),
                    textSize = 35.0,
                ) {
                    alignLeftToLeftOf(diamondIcon, 7.0)
                    diamondManager.state.onEach {
                        text = it.count.toString()
                        alignLeftToRightOf(diamondIcon, 6.0)
                        diamondContainer.alignLeftToLeftOf(alignmentContainer, 15)
                        diamondIcon.centerYOn(diamondContainer)
                    }.launchIn(CoroutineScope(Dispatchers.Default))
                }
            }
        }
        alignLeftToLeftOf(alignmentContainer, 15)
        alignTopToTopOf(alignmentContainer, 5)
    }
}
