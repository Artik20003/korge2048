package presentation.topbar

import com.soywiz.korge.view.*
import domain.score.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import presentation.*
import presentation.adapters.*

class CurrentScore(
    val scoreManager: ScoreManager,
    val alignmentContainer: Container
) : Container() {
    init {

        // Current Score
        text(
            text = ScoreTextAdapter.getTextByScore(scoreManager.state.value.score),
            textSize = 60.0,
            font = DefaultFontFamily.font,
        ) {
            scoreManager.state.onEach {
                text = ScoreTextAdapter.getTextByScore(it.score)
                centerOn(alignmentContainer)
            }.launchIn(CoroutineScope(Dispatchers.Default))
        }
    }
}
