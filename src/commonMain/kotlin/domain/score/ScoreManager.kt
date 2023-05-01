package domain.score

import com.soywiz.kbignum.*
import com.soywiz.korge.service.storage.NativeStorage
import data.*
import domain.playground.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ScoreManager(
    var playground: Playground,

) {
    private val storage: NativeStorage = DefaultStorage.storage

    var state = MutableStateFlow<ScoreState>(ScoreState())

    init {

        state.value = state.value.copy(
            bestScore = getBestScoreFromStorage(),
            score = getScoreFromStorage()
        )

        updateScore()

        state.onEach {
            storage["best"] = it.bestScore.toString()
            storage["score"] = it.score.toString()
        }.launchIn(CoroutineScope(Dispatchers.Unconfined))
    }

    private fun getBestScoreFromStorage(): BigInt {
        storage.getOrNull("best")?.let {
            return BigInt(it)
        }
        return BigInt.ZERO
    }
    private fun getScoreFromStorage(): BigInt {
        storage.getOrNull("score")?.let {
            return BigInt(it)
        }
        return BigInt.ZERO
    }

    private fun updateBestScoreIfPossible(score: BigInt) {
        if (score > state.value.bestScore) {
            state.value = state.value.copy(bestScore = score)
        }
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
    }

    fun clearScore() {
        setScore(BigInt.ZERO)
    }
}
