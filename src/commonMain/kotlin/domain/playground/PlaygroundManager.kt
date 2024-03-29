package domain.playground
import Constants
import com.soywiz.korge.service.storage.NativeStorage
import com.soywiz.korio.util.*
import data.*
import kotlinx.coroutines.flow.*
import kotlin.collections.set

class PlaygroundManager {
    var state = MutableStateFlow<PlaygroundState>(PlaygroundState())
        private set
    private var prevStateValue: PlaygroundState? = null
    private var staticHandlerList: MutableList<() -> Unit> = mutableListOf()
    private var cascadeHandlerList: MutableList<(cascadeCount: Int) -> Unit> = mutableListOf()
    private var collapsedHandlerList: MutableList<() -> Unit> = mutableListOf()
    private var endOfGameHandlerList: MutableList<() -> Unit> = mutableListOf()
    private var restartHandlerList: MutableList<() -> Unit> = mutableListOf()
    private var switchBlocksHandlerList: MutableList<() -> Unit> = mutableListOf()
    private var hammerRemoveBlockHandlerList: MutableList<() -> Unit> = mutableListOf()
    private val storage: NativeStorage = DefaultStorage.storage

    init {
        restorePlaygroundFromStorage()
    }

    fun addOnStaticStateListener(handler: () -> Unit) {
        staticHandlerList.add(handler)
    }

    private fun launchOnStaticStateHandlers() {
        staticHandlerList.forEach { it() }
    }

    fun addOnSwitchBlocksListener(handler: () -> Unit) {
        switchBlocksHandlerList.add(handler)
    }

    private fun launchOnSwitchBlocksHandlers() {
        switchBlocksHandlerList.forEach { it() }
    }

    fun addOnHammerRemoveBlockListener(handler: () -> Unit) {
        hammerRemoveBlockHandlerList.add(handler)
    }

    private fun launchOnHammerRemoveBlockHandlers() {
        hammerRemoveBlockHandlerList.forEach { it() }
    }

    fun addOnRestartListener(handler: () -> Unit) {
        restartHandlerList.add(handler)
    }

    private fun launchOnRestartHandlers() {
        restartHandlerList.forEach { it() }
    }

    fun addOnCollapsedStateListener(handler: () -> Unit) {
        collapsedHandlerList.add(handler)
    }

    private fun launchOnCollapsedStateHandlers() {
        collapsedHandlerList.forEach { it() }
    }

    fun addOnCascadeListener(handler: (cascadeCount: Int) -> Unit) {
        cascadeHandlerList.add(handler)
    }

    private fun launchOnCascadeHandlers() {
        cascadeHandlerList.forEach { it(state.value.currentCascadeCount) }
    }

    fun addOnEndOfGameListener(handler: () -> Unit) {
        endOfGameHandlerList.add(handler)
    }

    private fun launchOnEndOfGameHandlers() {
        endOfGameHandlerList.forEach { it() }
    }

    private fun restorePlaygroundFromStorage() {
        storage.getOrNull("playgroundBlocks")?.let {
            val serializedCols = it.split("|")
            val newBlocks = MutableList(Constants.Playground.COL_COUNT) { col ->
                val blockPower = serializedCols[col].split(":")
                if (blockPower.isEmpty() || serializedCols[col].isEmpty()) {
                    mutableListOf()
                } else {
                    blockPower.map {
                        PlaygroundBlock(power = it.toInt())
                    }.toMutableList()
                }
            }

            state.value = state.value.copy(
                playground = Playground(
                    blocks = newBlocks
                )
            )
        }
    }

    private fun savePlaygroundToStorage() {
        val serializedBlocks = state.value.playground.blocks
            .map {
                it.map { it.power }.joinToString(":")
            }
            .joinToString("|")
        storage["playgroundBlocks"] = serializedBlocks
    }

    fun push(column: Int, power: Int, callback: () -> Unit) {
        savePrevState()
        if (column !in 0 until Constants.Playground.COL_COUNT)
            throw IllegalArgumentException("Column number should be between 0..4, $column provided")
        if (state.value.playground.blocks[column].size >= Constants.Playground.ROW_COUNT + 1) return
        if (
            state.value.playground.blocks[column].size == Constants.Playground.ROW_COUNT &&
            power != (state.value.playground.blocks[column].lastOrNull()?.power ?: -1)
        ) return

        if (state.value.animationState != AnimationState.STATIC) return

        // start animation
        state.value = state.value.copy(
            // lastAddedColumn = column,
            animationState = AnimationState.NEW_BLOCK_PLACING,
        )

        val newPlaygroundBlock = PlaygroundBlock(
            power = power,
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
        preparePlaygroundForCollapsing()
        callback()
    }

    private fun savePrevState() {
        val prevBlocks = List<MutableList<PlaygroundBlock>>(Constants.Playground.COL_COUNT) {
            mutableListOf()
        }
        state.value.playground.blocks.forEachIndexed { col, colList ->

            colList.forEach { playgroundBlock ->
                prevBlocks[col].add(
                    PlaygroundBlock(
                        power = playgroundBlock.power,
                    )
                )
            }
        }
        prevStateValue = PlaygroundState(
            playground = Playground(
                blocks = prevBlocks
            ),

        )
    }

    fun setAnimationState(animationState: AnimationState) {
        when (animationState) {
            AnimationState.BLOCKS_COLLAPSING -> {
                // if nothing to collapse set STATIC animation state.value
                if (!state.value.hasBlocksToCollapse) {
                    setAnimationState(AnimationState.STATIC)
                    return
                }
                state.value = state.value.copy(currentCascadeCount = (state.value.currentCascadeCount + 1))
                launchOnCollapsedStateHandlers()
            }

            AnimationState.BLOCKS_MOVING -> {
                preparePlaygroundForMoving()
                if (!state.value.hasBlocksToMove) {
                    setAnimationState(AnimationState.STATIC)
                    return
                }
            }

            AnimationState.STATIC -> {
                moveBlocks()
                preparePlaygroundForCollapsing()
                if (state.value.hasBlocksToCollapse) {
                    setAnimationState(AnimationState.BLOCKS_COLLAPSING)
                    return
                }
                savePlaygroundToStorage()
                launchOnStaticStateHandlers()
                launchOnCascadeHandlers()

                state.value = state.value.copy(currentCascadeCount = 0)
            }

            AnimationState.BLOCKS_REMOVING -> {
                println("currenst state blocks removing")
                if (!state.value.hasBlocksToRemove) {
                    println("no blocks to remove")
                    setAnimationState(AnimationState.STATIC)
                    return
                }
            }

            else -> {
                Unit
            }
        }

        state.value = state.value.copy(animationState = animationState)
    }

    fun checkEndOfGame(nextUpcomingValue: Int) {
        if (!state.value.isPlaygroundFullOfBlocks)
            return

        val hasBlockToCollapseWithUpcoming = state.value.playground.blocks.map {
            it.last().power == nextUpcomingValue
        }.contains(true)

        if (!hasBlockToCollapseWithUpcoming)
            launchOnEndOfGameHandlers()
    }

    private fun blockExists(column: Int, row: Int): Boolean =
        state.value.playground.blocks.getOrNull(column)?.getOrNull(row) !== null

    private fun preparePlaygroundForCollapsing() {

        // all the figures except horizontal pair. In horizontal pair we need to know where to collapse
        val figures = listOf(
            // the order is critical. From biggest to smallest
            // -*-
            // *@*
            // -*-
            listOf(
                Pair(0, 1),
                Pair(1, 0),
                Pair(-1, 0),
                Pair(0, -1),
            ),

            // _*_
            // *@*
            listOf(
                Pair(0, -1),
                Pair(1, 0),
                Pair(-1, 0),
            ),
            // _*_
            // _@*
            // _*_
            listOf(
                Pair(0, -1),
                Pair(1, 0),
                Pair(0, 1),
            ),
            // _*_
            // *@-
            // _*_
            listOf(
                Pair(0, 1),
                Pair(-1, 0),
                Pair(0, -1),
            ),

            // *@*
            // _*_
            listOf(
                Pair(0, 1),
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
            // *
            // @
            // *
            listOf(
                Pair(0, -1),
                Pair(0, 1)
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
                Pair(1, 0)
            ),
            // _*
            // *@
            listOf(
                Pair(0, -1),
                Pair(-1, 0)
            ),
            // @
            // *
            listOf(
                Pair(0, 1)
            )

        )

        // finding figures to collapse
        figures.forEach { figure ->
            state.value.playground.iterateBlocks { col, row, block ->
                var hasFigure = true
                figure.forEach { shiftCoords ->
                    val checkingColumn = col + shiftCoords.first
                    val checkingRow = row + shiftCoords.second
                    if (
                        !(
                            blockExists(checkingColumn, checkingRow) &&
                                state.value.playground
                                .blocks[checkingColumn][checkingRow]
                                .power == block.power &&
                                state.value.playground
                                .blocks[checkingColumn][checkingRow]
                                .collapsingState == null
                            )
                    ) {
                        hasFigure = false
                    }
                }
                if (hasFigure) {
                    figure.forEach { shiftCoords ->
                        val checkingColumn = col + shiftCoords.first
                        val checkingRow = row + shiftCoords.second
                        state.value
                            .playground.blocks[checkingColumn][checkingRow]
                            .collapsingState = PlaygroundBlock.ChangingState(
                            targetCol = col,
                            targetRow = row
                        )
                    }
                    state.value.playground.blocks[col][row]
                        .collapsingState = PlaygroundBlock.ChangingState(
                        targetCol = col,
                        targetRow = row
                    )
                }
            }
        }

        // find horizontal pair
        state.value.playground.iterateBlocks { col, row, block ->
            if (
                blockExists(col - 1, row) &&
                state.value.playground.blocks[col - 1][row].power == block.power &&
                state.value.playground.blocks[col - 1][row].collapsingState == null &&
                state.value.playground.blocks[col][row].collapsingState == null
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

                val hasCollapsingStateNotSelf = block.collapsingState !== null &&
                    (
                        block.collapsingState?.targetCol != col ||
                            block.collapsingState?.targetRow != row
                        )

                if (hasCollapsingStateNotSelf || block.removingState)
                    shiftValue++
                else if (shiftValue > 0)
                    state.value.playground.blocks[col][row]
                        .movingState = PlaygroundBlock.ChangingState(
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

        val newPlaygroundBlocks: List<MutableList<PlaygroundBlock?>> =
            List(
                Constants.Playground.COL_COUNT
            ) {
                MutableList(Constants.Playground.ROW_COUNT) { null }
            }

        state.value.playground.iterateBlocks { col, row, block ->

            if (block.removingState) {
                return@iterateBlocks
            }

            block.collapsingState?.let { collapsingState ->
                if (
                    collapsingState.targetCol == col &&
                    collapsingState.targetRow == row
                ) {
                    val newCol = block.movingState?.targetCol ?: col
                    val newRow = block.movingState?.targetRow ?: row

                    newPlaygroundBlocks[newCol][newRow] =
                        PlaygroundBlock(
                            id = block.id,
                            power = block.targetPower ?: block.power,
                            isPrioritizedForCollapsing = true
                        )
                }
                return@iterateBlocks
            }

            block.movingState?.let { movingState ->
                newPlaygroundBlocks[movingState.targetCol][movingState.targetRow] =
                    PlaygroundBlock(
                        id = block.id,
                        power = block.power,
                        isPrioritizedForCollapsing = true,
                    )

                return@iterateBlocks
            }

            newPlaygroundBlocks[col][row] =
                PlaygroundBlock(
                    id = block.id,
                    power = block.power
                )
        }
        val newPlayground = Playground()
        newPlaygroundBlocks.forEachIndexed { col, list ->
            list.forEachIndexed { row, block ->
                block?.let {
                    newPlayground.blocks[col].add(it)
                }
            }
        }

        val newPlaygroundBlocksAnimatingState:
            MutableMap<UUID, PlaygroundBlockAnimatingState> = mutableMapOf()

        state.value.playground.iterateBlocks { _, _, block ->
            newPlaygroundBlocksAnimatingState[block.id] = PlaygroundBlockAnimatingState(
                animatingState = PlayBlockAnimationState.PLACED
            )
        }

        state.value = state.value.copy(
            playground = newPlayground,
            playgroundBlocksAnimatingState = newPlaygroundBlocksAnimatingState,
        )
    }

    private fun removeBlock(col: Int, row: Int) {
        savePrevState()
        state.value.playground.blocks[col][row].removingState = true
        state.value = state.value
    }

    fun hammerRemoveBlock(col: Int, row: Int) {
        removeBlock(col, row)
        launchOnHammerRemoveBlockHandlers()
        setAnimationState(AnimationState.BLOCKS_REMOVING)
    }

    fun switchBlocks(col1: Int, row1: Int, col2: Int, row2: Int) {
        savePrevState()

        state.value.playground.blocks[col1][row1].movingState = PlaygroundBlock.ChangingState(col2, row2)
        state.value.playground.blocks[col2][row2].movingState = PlaygroundBlock.ChangingState(col1, row1)
        state.value = state.value
        launchOnSwitchBlocksHandlers()
        setAnimationState(AnimationState.BLOCKS_MOVING)
    }

    fun removeBlocksByMinPower(minValidPower: Int) {
        state.value.playground.iterateBlocks { col, row, block ->
            if (block.power < minValidPower) {
                removeBlock(col, row)
            }
        }
        setAnimationState(AnimationState.BLOCKS_REMOVING)
    }

    fun revertState() {
        prevStateValue?.let {
            state.value = it
            prevStateValue = null
            setAnimationState(AnimationState.STATIC)
        }
    }

    fun restartGame() {
        state.value = PlaygroundState()
        setAnimationState(AnimationState.STATIC)
        launchOnRestartHandlers()
    }
}
