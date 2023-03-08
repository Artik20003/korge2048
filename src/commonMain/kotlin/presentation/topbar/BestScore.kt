package presentation.topbar

import com.soywiz.korge.view.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import domain.score.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import presentation.adapters.*

class BestScore(
    val scoreManager: ScoreManager,
    val alignmentContainer: Container
) : Container() {
    init {
        launchImmediately(Dispatchers.Unconfined) {
            container {
                val bestScoreContainer = this
                val crownIcon = image(texture = resourcesVfs["/icons/crown.svg"].readSVG().render()) {
                    scale = .02
                }

                text(
                    text = ScoreTextAdapter.getTextByScore(scoreManager.state.value.bestScore),
                    textSize = 35.0,
                ) {
                    alignLeftToRightOf(crownIcon, 7.0)
                    positionY(13)
                    scoreManager.state.onEach {
                        text = ScoreTextAdapter.getTextByScore(it.bestScore)
                        alignLeftToRightOf(crownIcon, 6.0)
                        bestScoreContainer.alignRightToRightOf(alignmentContainer, 15)
                    }.launchIn(CoroutineScope(Dispatchers.Default))
                }
                alignRightToRightOf(alignmentContainer, 15)
                alignTopToTopOf(alignmentContainer, 5)
            }
        }
    }
}
