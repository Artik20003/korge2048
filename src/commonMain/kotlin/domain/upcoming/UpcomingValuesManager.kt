package domain.upcoming

import kotlinx.coroutines.flow.*
import kotlin.random.*

class UpcomingValuesManager {

    var state = MutableStateFlow<UpcomingValuesState>(UpcomingValuesState())

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
        return Random(3).nextInt(state.value.upcomingMin, state.value.upcomingMax)
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
