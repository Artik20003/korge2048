package domain.diamond

import Constants
import com.soywiz.korge.service.storage.NativeStorage
import data.*
import domain.level.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class DiamondManager(val levelManager: LevelManager) {
    private val currentHammerPrice: Int
        get() = Constants.Diamond.START_HAMMER_PRICE + levelManager.state.value.level * 10
    private val currentSwitchPrice: Int
        get() = Constants.Diamond.START_SWITCH_PRICE + levelManager.state.value.level * 10

    var state = MutableStateFlow<DiamondState>(DiamondState())
        private set
    private val storage: NativeStorage = DefaultStorage.storage

    init {
        // storage["diamonds"] = 1000.toString()
        levelManager.state.onEach {
            state.update {
                state.value.copy(
                    currentHammerPrice = currentHammerPrice,
                    currentSwitchPrice = currentSwitchPrice
                )
            }
        }.launchIn(CoroutineScope(Dispatchers.Default))

        storage.getOrNull("diamonds")?.let { count ->
            state.update { state.value.copy(count = count.toInt()) }
        }

        state.onEach {
            storage["diamonds"] = it.count.toString()
        }.launchIn(CoroutineScope(Dispatchers.Default))
    }

    fun addDiamonds(count: Int) {
        state.value = state.value.copy(count = state.value.count + count)
    }

    private fun subtractDiamonds(count: Int) {
        if (isSubtractionAvailable(count)) {
            state.value = state.value.copy(count = state.value.count - count)
        }
    }

    fun pay(payForAction: PayableDiamondAction) {
        when (payForAction) {
            PayableDiamondAction.SWITCH ->
                subtractDiamonds(currentSwitchPrice)
            PayableDiamondAction.HAMMER ->
                subtractDiamonds(currentHammerPrice)
        }
    }

    private fun isSubtractionAvailable(subtractionCount: Int): Boolean {
        return state.value.count - subtractionCount >= 0
    }
}

enum class PayableDiamondAction {
    SWITCH,
    HAMMER,
}
