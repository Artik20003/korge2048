package domain

import com.soywiz.korio.util.*

data class PlaygroundState(

    val playground:Playground = Playground(
        blocks =   listOf(
            /*

                        mutableListOf(),
                        mutableListOf(),
                        mutableListOf(),
                        mutableListOf(),
                        mutableListOf(),
            */



            mutableListOf(
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
            mutableListOf(),

            ),
    ),

    var level: Int = 1,
    val score: Long = 0,

    var playgroundBlocksAnimatingState: Map<UUID, PlaygroundBlockAnimatingState> = emptyMap(),

    val upcomingValues: List<Int> = emptyList(),

    val lastAddedColumn: Int? = null,
    val animationState: AnimationState = AnimationState.STATIC,

    ){


    val upcomingMin: Int
        get() = level
    val upcomingMax: Int
        get() = upcomingMin + Constants.Playground.AVAILABLE_GENERATING_SPREAD

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

    val currentBlocksCount: Int
        get() {
            var count = 0
            playground.blocks.map { it.size }.forEach { count += it }
            return count
        }

    init {
        if(playgroundBlocksAnimatingState.isEmpty()){
            var  initialPlaygroundBlocksAnimatingState:
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
}

data class PlaygroundBlockAnimatingState(
    var animatingState: PlayBlockAnimationState,
)

enum class PlayBlockAnimationState(){
    BOTTOM,
    PLACED,
    MOVED,
    COLLAPSED,
    DISAPPEARED

}
