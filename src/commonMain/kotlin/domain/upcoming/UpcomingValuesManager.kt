package domain.upcoming

import kotlin.random.*
import kotlinx.coroutines.flow.*

class UpcomingValuesManager {

    var state = MutableStateFlow<UpcomingValuesState>(UpcomingValuesState())
    private var random = Random(3)
    private var onGenerateUpcomingValuesHandlerList: MutableList<() -> Unit> = mutableListOf()

    init {
        generateUpcomingValues()
    }

    fun updateLevelUpcomingValues(level: Int) {
        setMinUpcomingValue(level)
        removeUpcomingValues()
        generateUpcomingValues()
    }

    private fun removeUpcomingValues() {
        state.value = state.value.copy(upcomingValues = emptyList())
    }

    private fun setMinUpcomingValue(value: Int) {
        state.value = state.value.copy(upcomingMin = value)
    }

    private fun getGeneratedValue(): Int {
        return random.nextInt(state.value.upcomingMin, state.value.upcomingMax)
    }

    fun generateUpcomingValues() {
        state.update {
            if (state.value.upcomingValues.isEmpty()) {
                it.copy(
                    upcomingValues = listOf(getGeneratedValue(), getGeneratedValue())
                )
            } else {
                it.copy(
                    upcomingValues = listOf(it.upcomingValues[1], getGeneratedValue())
                )
            }
        }
        launchOnGenerateUpcomingValues()
    }

    fun addOnGenerateUpcomingValuesListener(handler: () -> Unit) {
        onGenerateUpcomingValuesHandlerList.add(handler)
    }
    fun launchOnGenerateUpcomingValues() {
        onGenerateUpcomingValuesHandlerList.forEach { it() }
    }
}
