package presentation

import Constants
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korio.util.*
import domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


@OptIn(FlowPreview::class)
class PlayScene(val playgroundManager: PlaygroundManager) : Scene() {
    val cellSize = 50

    var onNewBlockAnimationFinishedFlag = MutableStateFlow(false)
    var onCollapseBlockAnimationFinishedFlag = MutableStateFlow(false)
    var onMoveBlockAnimationFinishedFlag = MutableStateFlow(false)
    var blocks: MutableMap<UUID, UIPlaygroundBlock> = mutableMapOf()
    var playground: Container? = null
    override suspend fun SContainer.sceneMain() {

        setOnEndAnimationHandlers()

        //!!TODO launch only if animationState changed
        playgroundManager.state.debounce(20).onEach { state ->

            updateUIBlockState()
            when (state.animationState) {
                AnimationState.NEW_BLOCK_PLACING,
                AnimationState.STATIC, -> redrawPlayground()
                AnimationState.BLOCKS_COLLAPSING -> {
                    blocks.forEach { it.value.collapseIfNeeded() }
                }
                AnimationState.BLOCKS_MOVING -> {
                    blocks.forEach { it.value.moveIfNeeded() }
                }
            }

        }.launchIn(CoroutineScope(Dispatchers.Default))

    }

    private fun setOnEndAnimationHandlers() {

        onNewBlockAnimationFinishedFlag.debounce(100).onEach { flag ->
            if(flag){
                playgroundManager.setAnimationState(AnimationState.BLOCKS_COLLAPSING)
                onNewBlockAnimationFinishedFlag.value = false
            }
        }.launchIn(CoroutineScope(Dispatchers.Default))

        onCollapseBlockAnimationFinishedFlag.debounce(100).onEach { flag ->
            if(flag){
                playgroundManager.setAnimationState(AnimationState.BLOCKS_MOVING)
                onCollapseBlockAnimationFinishedFlag.value = false
            }
        }.launchIn(CoroutineScope(Dispatchers.Default))

        onMoveBlockAnimationFinishedFlag.debounce(100).onEach { flag ->
            if(flag){
                playgroundManager.setAnimationState(AnimationState.STATIC)
                onMoveBlockAnimationFinishedFlag.value = false
            }
        }.launchIn(CoroutineScope(Dispatchers.Default))
    }

    fun SContainer.redrawPlayground(){
        blocks = mutableMapOf()

        val newPlayground = container {
            playgroundManager.iterateBlocks { col, row, block ->
                val playgroundBlock = playgroundBlock(
                    col = col,
                    row = row,
                    power = block.power,
                    animationState = playgroundManager.state.value.playgroundBlocksAnimatingState[block.id]!!
                        .animatingState,
                    targetPower = block.targetPower,
                    collapsingState = block.collapsingState,
                    movingState = block.movingState,
                    playgroundAnimationState = playgroundManager.state.value.animationState,
                    onNewBlockAnimationFinished = { onNewBlockAnimationFinishedFlag.value = true },
                    onCollapseBlockAnimationFinished = { onCollapseBlockAnimationFinishedFlag.value = true },
                    onMoveBlockAnimationFinished = { onMoveBlockAnimationFinishedFlag.value = true }
                )
                blocks[block.id] = playgroundBlock
            }
        }
        playground?.removeFromParent()
        playground = newPlayground
        for (colNum in 0 until Constants.Playground.COL_COUNT) {
            clickableColumn(
                onClick = {
                    playgroundManager.push(colNum)
                }
            )
            .position(
                x = colNum * cellSize,
                y = 0
            )
        }

        text(playgroundManager.state.value.animationState.toString()).position(0, cellSize* Constants.Playground.ROW_COUNT + 2)

    }

    fun updateUIBlockState () {

        playgroundManager.iterateBlocks { col, row, block ->
            blocks[block.id]?.col = col
            blocks[block.id]?.row = row
            blocks[block.id]?.power = block.power
            blocks[block.id]?.animationState = playgroundManager.state.value.playgroundBlocksAnimatingState[block.id]!!
                    .animatingState
            blocks[block.id]?.targetPower = block.targetPower
            blocks[block.id]?.collapsingState = block.collapsingState
            blocks[block.id]?.movingState = block.movingState
            blocks[block.id]?.playgroundAnimationState = playgroundManager.state.value.animationState
            blocks[block.id]?.onNewBlockAnimationFinished = { onNewBlockAnimationFinishedFlag.value = true }
            blocks[block.id]?.onCollapseBlockAnimationFinished = { onCollapseBlockAnimationFinishedFlag.value = true }
            blocks[block.id]?.onMoveBlockAnimationFinished = { onMoveBlockAnimationFinishedFlag.value = true }

        }
    }


}
