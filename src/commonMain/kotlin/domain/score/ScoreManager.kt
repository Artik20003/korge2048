package domain.score

import com.soywiz.korge.service.storage.*
import domain.playground.*
import kotlin.math.*
import kotlinx.coroutines.flow.*

class ScoreManager(
    var playground: Playground,
    val storage: NativeStorage
) {

    var state = MutableStateFlow<ScoreState>(ScoreState())

    init {
        updateScore()
    }

    fun getBestScore(): Int {
        return storage.getOrNull("best")?.toInt() ?: 0
    }

    private fun setBestScore(score: Int) {
        storeBestScore(score)
    }

    private fun storeBestScore(score: Int) {
        storage["best"] = score.toString()
    }

    private fun updateBestScoreIfPossible(score: Int) {
        if (score > getBestScore()) {
            setBestScore(score)
        }
    }

    fun getScore(): Int {
        return state.value.score
    }

    private fun setScore(score: Int) {
        state.value = state.value.copy(score = score)
    }

    fun updateScore() {
        var scoreSpread = 0

        playground.iterateBlocks { col, row, block ->
            block.collapsingState?.let { collapsingState ->
                if (collapsingState.targetCol == col && collapsingState.targetRow == row) {
                    block.targetPower?.let { targetPower ->
                        scoreSpread += 2.0.pow(targetPower).toInt() * (targetPower - block.power)
                    }
                }
            }
        }

        setScore(state.value.score + scoreSpread)
        updateBestScoreIfPossible(state.value.score)
        println(
            "Current score: ${getScore()} Best Score: ${getBestScore()} "
        )
    }
}
