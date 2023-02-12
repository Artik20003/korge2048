package domain.playground
import com.soywiz.korio.util.*
import kotlin.collections.set

data class PlaygroundState(

    val playground: Playground = Playground(
        blocks = listOf(

            mutableListOf(
                PlaygroundBlock(power = 21),
                PlaygroundBlock(power = 20),
            ),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(
                PlaygroundBlock(power = 20),
            ),
            mutableListOf(
                PlaygroundBlock(power = 22),
                PlaygroundBlock(power = 21),
            ),

         /*   mutableListOf(
                PlaygroundBlock(power = 7),
            ),
            mutableListOf(
                PlaygroundBlock(power = 5),
                PlaygroundBlock(power = 3),
            ),
            mutableListOf(
                PlaygroundBlock(power = 3),
                PlaygroundBlock(power = 5),
                PlaygroundBlock(power = 3),
            ),
            mutableListOf(),
            mutableListOf(),*/

        ),
    ),

    // var level: Int = 1,
    val score: Long = 0,

    var playgroundBlocksAnimatingState: Map<UUID, PlaygroundBlockAnimatingState> = emptyMap(),
    val lastAddedColumn: Int? = null,
    val animationState: AnimationState = AnimationState.STATIC,

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

    val currentBlocksCount: Int
        get() {
            var count = 0
            playground.blocks.map { it.size }.forEach { count += it }
            return count
        }

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
