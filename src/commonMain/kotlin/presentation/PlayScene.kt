package presentation

import Constants
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korio.util.*
import domain.*
import domain.level.*
import domain.playground.*
import domain.score.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


@OptIn(FlowPreview::class)
class PlayScene() : Scene() {
    val cellSize = СellSizeAdapter.cellSize
    val playgroundManager: PlaygroundManager = PlaygroundManager()
    val levelManager: LevelManager = LevelManager(playgroundManager.state.value.playground)
    val scoreManager: ScoreManager = ScoreManager(playgroundManager.state.value.playground)
    var onNewBlockAnimationFinishedFlag = MutableStateFlow(false)
    var onCollapseBlockAnimationFinishedFlag = MutableStateFlow(false)
    var onMoveBlockAnimationFinishedFlag = MutableStateFlow(false)
    var blocks: MutableMap<UUID, UIPlaygroundBlock> = mutableMapOf()
    var playground: Container? = null

    override suspend fun SContainer.sceneMain() {

        setOnEndAnimationHandlers()
        playgroundManager.addOnStaticStateListener {
            levelManager.playground = playgroundManager.state.value.playground
            levelManager.upgradeLevelIfNeeded()
        }

        playgroundManager.addOnCollapsedStateListener {
            scoreManager.playground = playgroundManager.state.value.playground
            scoreManager.UpdateScore()
        }

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

        levelManager.state.onEach {
            println( "Setting new min upcoming value: ${it.level}")
            playgroundManager.setMinUpcomingValue(it.level)
        }.launchIn(CoroutineScope(Dispatchers.Default))


        // UI score
        text(scoreManager.state.value.score.toString(), textSize = 16.0){
            scoreManager.state.onEach {
                text = it.score.toString()
            }.launchIn(CoroutineScope(Dispatchers.Default))
        }

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
            playgroundManager.state.value.playground.iterateBlocks { col, row, block ->
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
                x = (colNum * cellSize).toInt(),
                y = 0
            )
        }

        text(playgroundManager.state.value.animationState.toString()).position(0, (cellSize* Constants.Playground.ROW_COUNT + 2).toInt())

    }

    fun updateUIBlockState () {

        playgroundManager.state.value.playground.iterateBlocks { col, row, block ->
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
