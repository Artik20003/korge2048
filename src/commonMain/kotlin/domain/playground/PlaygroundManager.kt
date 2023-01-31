package domain.playground

import Constants
import com.soywiz.korio.util.*
import domain.playground.AnimationState.*
import kotlinx.coroutines.flow.*
import kotlin.collections.set
import kotlin.random.*

class PlaygroundManager {
    var state = MutableStateFlow<PlaygroundState>(PlaygroundState())
        private set
    private var staticHandlerList: MutableList<() -> Unit> = mutableListOf()

    init {
        generateUpcomingValues()
    }

    fun addOnStaticStateListener(handler: () -> Unit) {
        staticHandlerList.add(handler)
    }

    fun launchOnStaticStateHandlers() {
        staticHandlerList.forEach { it() }
    }

    fun setMinUpcomingValue(value: Int){
        state.value = state.value.copy(upcomingMin = value)
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

    private fun getGeneratedValue(): Int {
        return Random(3).nextInt(state.value.upcomingMin, state.value.upcomingMax)
    }

    fun push(column: Int) {

        if (column !in 0 until Constants.Playground.COL_COUNT)
            throw IllegalArgumentException("Column number should be between 0..4, $column provided")
        if (state.value.playground.blocks[column].size >= Constants.Playground.ROW_COUNT) return
        if (state.value.animationState != STATIC) return

        //start animation
        state.value = state.value.copy(
            lastAddedColumn = column,
            animationState = NEW_BLOCK_PLACING,
        )

        val newPlaygroundBlock = PlaygroundBlock(
            power = state.value.upcomingValues[0],
            isPrioritizedForCollapsing = true
        )
        state.value.playground.blocks[column].add(
            newPlaygroundBlock
        )

        state.value.playgroundBlocksAnimatingState +=
            Pair(
                newPlaygroundBlock.id,
                PlaygroundBlockAnimatingState(
                    animatingState = PlayBlockAnimationState.BOTTOM
                )
            )


        state.value = state.value.copy(
            playground = state.value.playground
        )
        generateUpcomingValues()
        preparePlaygroundForCollapsing()

        //updatePlaygroundAnimationState()


    }


    fun setAnimationState(animationState: AnimationState) {
        when (animationState) {
            BLOCKS_COLLAPSING -> {
                // if nothing to collapse set STATIC animation state.value
                if (!state.value.hasBlocksToCollapse) {
                    setAnimationState(STATIC)
                    return
                }
            }

            BLOCKS_MOVING -> {
                preparePlaygroundForMoving()
                if (!state.value.hasBlocksToMove) {
                    setAnimationState(STATIC)
                    return
                }
            }

            STATIC -> {
                moveBlocks()
                preparePlaygroundForCollapsing()
                if (state.value.hasBlocksToCollapse) {
                    setAnimationState(BLOCKS_COLLAPSING)
                    return
                }
                launchOnStaticStateHandlers()
            }

            else -> Unit
        }

        state.value = state.value.copy(animationState = animationState)


    }

    private fun blockExists(column: Int, row: Int): Boolean =
        state.value.playground.blocks.getOrNull(column)?.getOrNull(row) !== null

    private fun preparePlaygroundForCollapsing() {

        // all the figures except horizontal pair. In horizontal pair we need to know where to collapse
        val figures = listOf(
            // the order is critical. From biggest to smallest
            //_*_
            //*@*
            listOf(
                Pair(0, -1),
                Pair(1, 0),
                Pair(-1, 0),
            ),
            // *_
            // @*
            listOf(
                Pair(0, -1),
                Pair(1, 0)
            ),
            // *@*
            listOf(
                Pair(-1, 0),
                Pair(1, 0)
            ),
            // *@
            // _*
            listOf(
                Pair(0, 1),
                Pair(-1, 0)
            ),
            // @*
            // *_
            listOf(
                Pair(0, 1),
                Pair(-1, 0)
            ),
            // _*
            // *@
            listOf(
                Pair(0, -1),
                Pair(-1, 0)
            ),
            //@
            //*
            listOf(
                Pair(0, 1)
            )

        )

        // finding figures to collapse
        figures.forEach { figure ->
            state.value.playground.iterateBlocks { col, row, block ->
                var hasFigure = true
                figure.forEach { shiftCoords ->
                    if (
                        !(
                            blockExists(col + shiftCoords.first, row + shiftCoords.second)
                                && state.value.playground.blocks[col + shiftCoords.first][row + shiftCoords.second].power ==
                                block.power
                                && state.value.playground.blocks[col + shiftCoords.first][row + shiftCoords.second].collapsingState == null
                            )
                    ) {
                        hasFigure = false
                    }
                }
                if (hasFigure) {
                    figure.forEach { shiftCoords ->
                        state.value.playground.blocks[col + shiftCoords.first][row + shiftCoords.second].collapsingState =
                            PlaygroundBlock.ChangingState(
                                targetCol = col,
                                targetRow = row
                            )
                    }
                    state.value.playground.blocks[col][row].collapsingState = PlaygroundBlock.ChangingState(
                        targetCol = col,
                        targetRow = row
                    )
                }
            }
        }

        // find horizontal pair
        state.value.playground.iterateBlocks { col, row, block ->
            if (
                blockExists(col - 1, row)
                && state.value.playground.blocks[col - 1][row].power == block.power
                && state.value.playground.blocks[col - 1][row].collapsingState == null
                && state.value.playground.blocks[col][row].collapsingState == null
            ) {
                val collapsingState: PlaygroundBlock.ChangingState?
                if (block.isPrioritizedForCollapsing) {
                    collapsingState = PlaygroundBlock.ChangingState(
                        targetRow = row,
                        targetCol = col
                    )
                } else if (state.value.playground.blocks[col - 1][row].isPrioritizedForCollapsing) {
                    collapsingState = PlaygroundBlock.ChangingState(
                        targetRow = row,
                        targetCol = col - 1
                    )
                } else { // If both prioritized -> move to center
                    if (col <= 2)
                        collapsingState = PlaygroundBlock.ChangingState(
                            targetRow = row,
                            targetCol = col
                        )
                    else
                        collapsingState = PlaygroundBlock.ChangingState(
                            targetRow = row,
                            targetCol = col - 1
                        )
                }
                state.value.playground.blocks[col - 1][row].collapsingState = collapsingState
                state.value.playground.blocks[col][row].collapsingState = collapsingState

            }
        }


        // set target power to block that will be collapsed into
        state.value.playground.iterateBlocks { col, row, block ->
            block.collapsingState?.let { changingState ->
                if (changingState.targetCol != col || changingState.targetRow != row)
                    state.value.playground.blocks[changingState.targetCol][changingState.targetRow]
                        .targetPowerShift++
            }
        }
        // set target power to block that will be disappeared
        state.value.playground.iterateBlocks { col, row, block ->
            block.collapsingState?.let { changingState ->
                state.value.playground.blocks[col][row].targetPowerShift =
                    state.value
                        .playground.blocks[changingState.targetCol][changingState.targetRow]
                        .targetPowerShift
            }
        }
        state.value = state.value.copy(
            playground = state.value.playground,
        )

    }

    fun preparePlaygroundForMoving() {

        for (col in 0 until Constants.Playground.COL_COUNT) {
            var shiftValue = 0
            state.value.playground.blocks[col].forEachIndexed { row, block ->
                if (block.collapsingState !== null &&
                    (
                        block.collapsingState?.targetCol != col ||
                            block.collapsingState?.targetRow != row
                        )
                )
                    shiftValue++
                else if (shiftValue > 0)
                    state.value.playground.blocks[col][row].movingState = PlaygroundBlock.ChangingState(
                        targetCol = col,
                        targetRow = row - shiftValue
                    )
            }
        }
        state.value = state.value.copy(
            playground = state.value.playground,
        )


    }


    private fun moveBlocks() {

        val newPlayground = Playground()
        state.value.playground.iterateBlocks { col, row, block ->
            block.collapsingState?.let { collapsingState ->
                if (
                    collapsingState.targetCol == col &&
                    collapsingState.targetRow == row
                ) {

                    newPlayground.blocks[col].add(
                        //block.movingState?.targetRow ?: row,
                        PlaygroundBlock(
                            id = block.id,
                            power = block.targetPower ?: block.power,
                            isPrioritizedForCollapsing = true
                        )
                    )
                }
                return@iterateBlocks
            }
            block.movingState?.let { movingState ->
                newPlayground.blocks[movingState.targetCol].add(
                    //movingState.targetRow,
                    PlaygroundBlock(
                        id = block.id,
                        power = block.power,
                        isPrioritizedForCollapsing = true
                    )
                )
                return@iterateBlocks
            }
            newPlayground.blocks[col].add(
                PlaygroundBlock(
                    id = block.id,
                    power = block.power
                )
            )

        }

        val newPlaygroundBlocksAnimatingState:
            MutableMap<UUID, PlaygroundBlockAnimatingState> = mutableMapOf()

        state.value.playground.iterateBlocks { col, row, block ->
            newPlaygroundBlocksAnimatingState[block.id] = PlaygroundBlockAnimatingState(
                animatingState = PlayBlockAnimationState.PLACED
            )
        }


        state.value = state.value.copy(
            playground = newPlayground,
            playgroundBlocksAnimatingState = newPlaygroundBlocksAnimatingState,
        )

    }


    /*
    private fun logPlayground(){
        var log = "  "

        for(col in 0..Constants.Playground.COL_COUNT - 1) {
            log +="$col|"
        }
        log += "\n  "
        for(col in 0..Constants.Playground.COL_COUNT - 1) {
            log += "__"
        }
        log += "\n"
        for(row in 0..Constants.Playground.ROW_COUNT - 1){
            log += "$row|"
            for(col in 0..Constants.Playground.COL_COUNT - 1) {
                val printVal = state.value.playground.blocks[col].getOrNull(row)?: '*'
                log += "$printVal|"
            }
            log += "\n"

        }
        println(log)
    }
     */


}
