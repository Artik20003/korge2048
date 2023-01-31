package domain.level

import Constants
import domain.playground.*
import kotlinx.coroutines.flow.*

class LevelManager(var playground: Playground) {
    var state = MutableStateFlow<LevelState>(LevelState())
        private set

    init {
        upgradeLevelIfNeeded()
    }

    private fun isIncrementLevelNeeded(): Boolean {
        var isIncrementLevelNeeded = false
        playground.iterateBlocks { col, row, block ->
            if (block.power >= state.value.level + Constants.Playground.NEXT_LEVEL_SPREAD) {
                isIncrementLevelNeeded = true
                return@iterateBlocks
            }
        }
        return isIncrementLevelNeeded
    }

    fun upgradeLevelIfNeeded() {
        // debug
        playground.iterateBlocks { col, row, block ->
            print(block.power)
        }
        println("==")

        // end  debug

        if (isIncrementLevelNeeded()) {
            var maxPower = 1
            playground.iterateBlocks { col, row, block ->
                if (block.power > maxPower)
                    maxPower = block.power
            }
            setLevel(maxPower - Constants.Playground.NEXT_LEVEL_SPREAD)
        }
    }

    private fun setLevel(level: Int) {
        state.value = state.value.copy(level = level)
    }
}
