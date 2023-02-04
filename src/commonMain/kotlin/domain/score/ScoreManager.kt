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
        initBestScore()
        updateScore()
    }

    private fun initBestScore() {
        state.value = state.value.copy(
            bestScore = getBestScoreFromStorage()
        )
    }

    private fun getBestScoreFromStorage(): Int {
        return storage.getOrNull("best")?.toInt() ?: 0
    }

    fun getBestScore(): Int {
        return state.value.bestScore
    }

    private fun setBestScore(score: Int) {
        state.value = state.value.copy(bestScore = score)
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
            "Current score: " + "${getScore()} " +
                "Best Score: " + "${getBestScore()}" +
                " Storage best score: ${getBestScoreFromStorage()} "
        )
    }
}
