package domain.score

import com.soywiz.kbignum.*
import com.soywiz.korge.service.storage.*
import domain.playground.*
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

    private fun getBestScoreFromStorage(): BigInt {
        storage.getOrNull("best")?.let {
            return BigInt(it)
        }
        return BigInt.ZERO
    }

    fun getBestScore(): BigInt {
        return state.value.bestScore
    }

    private fun setBestScore(score: BigInt) {
        state.value = state.value.copy(bestScore = score)
        storeBestScore(score)
    }

    private fun storeBestScore(score: BigInt) {
        storage["best"] = score.toString()
    }

    private fun updateBestScoreIfPossible(score: BigInt) {
        if (score > getBestScore()) {
            setBestScore(score)
        }
    }

    fun getScore(): BigInt {
        return state.value.score
    }

    private fun setScore(score: BigInt) {
        state.value = state.value.copy(score = score)
    }

    fun updateScore() {
        var scoreSpread: BigInt = BigInt.ZERO

        playground.iterateBlocks { col, row, block ->
            block.collapsingState?.let { collapsingState ->
                if (collapsingState.targetCol == col && collapsingState.targetRow == row) {
                    block.targetPower?.let { targetPower ->
                        scoreSpread += BigInt(2).pow(targetPower) * (targetPower - block.power)
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
