package domain.playground

import com.soywiz.korio.util.*

data class PlaygroundBlock(
    val power: Int,
    val id: UUID = UUID.randomUUID(),
    var collapsingState: ChangingState? = null,
    var movingState: ChangingState? = null,
    var targetPowerShift: Int = 0,
    var isPrioritizedForCollapsing: Boolean = false,

) {
    data class ChangingState(
        val targetCol: Int,
        val targetRow: Int,
    ) {
        override fun toString(): String = "($targetCol, $targetRow)"
    }
    val targetPower: Int?
        get() = if (targetPowerShift == 0) null else targetPowerShift + power
}
