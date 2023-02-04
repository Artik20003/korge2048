package domain.upcoming

import kotlin.random.*
import kotlinx.coroutines.flow.*

class UpcomingValuesManager {

    var state = MutableStateFlow<UpcomingValuesState>(UpcomingValuesState())

    init {
        generateUpcomingValues()
    }
    fun popCurrentUpcomingValue(): Int {
        val currentValue = state.value.upcomingValues[0]
        generateUpcomingValues()
        return currentValue
    }

    fun setMinUpcomingValue(value: Int) {
        state.value = state.value.copy(upcomingMin = value)
    }
    private fun getGeneratedValue(): Int {
        return Random(3).nextInt(state.value.upcomingMin, state.value.upcomingMax)
    }
    private fun generateUpcomingValues() {
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
    }
}
