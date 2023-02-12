package domain.playground
import com.soywiz.korio.util.*
import kotlin.collections.set

data class PlaygroundState(

    val playground: Playground = Playground(
        blocks = listOf(
            mutableListOf(
                PlaygroundBlock(power = 8),
                PlaygroundBlock(power = 7),
                PlaygroundBlock(power = 6),
                PlaygroundBlock(power = 5),
                PlaygroundBlock(power = 4),
                PlaygroundBlock(power = 3),
                PlaygroundBlock(power = 2),
            ),
            mutableListOf(
                /*
                PlaygroundBlock(power = 7),
                PlaygroundBlock(power = 6),
                PlaygroundBlock(power = 5),
                PlaygroundBlock(power = 4),
                PlaygroundBlock(power = 3),
                PlaygroundBlock(power = 2),

                 */
            ),
            mutableListOf(

                PlaygroundBlock(power = 6),
                PlaygroundBlock(power = 5),
                PlaygroundBlock(power = 4),
                PlaygroundBlock(power = 3),
                PlaygroundBlock(power = 2),

            ),
            mutableListOf(
                /*
                PlaygroundBlock(power = 5),
                PlaygroundBlock(power = 4),
                PlaygroundBlock(power = 3),
                PlaygroundBlock(power = 2),

                 */
            ),
            mutableListOf(
                PlaygroundBlock(power = 5),
                PlaygroundBlock(power = 4),
                PlaygroundBlock(power = 3),
            ),
        ),
    ),

    // var level: Int = 1,
    // val score: Long = 0,

    var playgroundBlocksAnimatingState: Map<UUID, PlaygroundBlockAnimatingState> = emptyMap(),
    val lastAddedColumn: Int? = null,
    val animationState: AnimationState = AnimationState.STATIC,
    val currentCascadeCount: Int = 0

) {
    val hasBlocksToCollapse: Boolean
        get() =
            playground.blocks.map {
                val havingChangingStateElem = it.find { it.collapsingState !== null }
                havingChangingStateElem != null
            }.contains(true)
    val hasBlocksToMove: Boolean
        get() =
            playground.blocks.map {
                val havingChangingStateElem = it.find { it.movingState !== null }
                havingChangingStateElem != null
            }.contains(true)

    val hasBlocksToRemove: Boolean
        get() =
            playground.blocks.map {
                val hasRemovingState = it.find { it.removingState == true }
                hasRemovingState != null
            }.contains(true)

    init {
        if (playgroundBlocksAnimatingState.isEmpty()) {
            val initialPlaygroundBlocksAnimatingState:
                MutableMap<UUID, PlaygroundBlockAnimatingState> = mutableMapOf()
            playground.blocks.forEach {
                it.forEach {
                    initialPlaygroundBlocksAnimatingState[it.id] =
                        PlaygroundBlockAnimatingState(
                            animatingState = PlayBlockAnimationState.PLACED
                        )
                }
            }
            playgroundBlocksAnimatingState = initialPlaygroundBlocksAnimatingState
        }
    }
}

enum class AnimationState() {
    NEW_BLOCK_PLACING,
    BLOCKS_COLLAPSING,
    BLOCKS_MOVING,
    STATIC,
    BLOCKS_REMOVING,
}

data class PlaygroundBlockAnimatingState(
    var animatingState: PlayBlockAnimationState,
)

enum class PlayBlockAnimationState() {
    BOTTOM,
    PLACED,
    MOVED,
    COLLAPSED,
}
