package domain.upcoming

data class UpcomingValuesState(
    val upcomingValues: List<Int> = emptyList(),
    var upcomingMin: Int = 1,

) {
    val upcomingMax: Int
        get() = upcomingMin + Constants.Playground.AVAILABLE_GENERATING_SPREAD
}
