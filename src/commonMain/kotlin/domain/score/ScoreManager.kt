package domain.score

import domain.playground.*
import kotlinx.coroutines.flow.*
import kotlin.math.*

class ScoreManager(var playground: Playground) {
    var state = MutableStateFlow<ScoreState>(ScoreState())


    init {
        UpdateScore()
    }

    private fun SetScore(score: Int) {
        state.value = state.value.copy(score=score)
    }

    fun UpdateScore() {
        var scoreSpread = 0

        playground.iterateBlocks { col, row, block ->
            block.collapsingState?.let { collapsingState ->
                if (collapsingState.targetCol == col && collapsingState.targetRow == row) {
                    block.targetPower?.let {targetPower ->
                        scoreSpread += 2.0.pow(targetPower).toInt() * (targetPower - block.power)
                    }

                }
            }
        }

        SetScore(state.value.score + scoreSpread)
    }


}
