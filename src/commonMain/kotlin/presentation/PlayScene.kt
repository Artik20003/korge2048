package presentation

import Constants
import com.soywiz.klock.*
import com.soywiz.korge.input.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.onClick
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.interpolation.*
import domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


class PlayScene(val playgroundManager: PlaygroundManager) : Scene() {
    val cellSize = 50
    var playgroundView: View? = null
    var onCollapseBlockAnimationFinishedFlag = MutableStateFlow(false)
    override suspend fun SContainer.sceneMain() {


        onCollapseBlockAnimationFinishedFlag.debounce(100).onEach { flag ->
            if(flag){
                println(111111111)
                playgroundManager.setAnimationState(AnimationState.BLOCKS_COLLAPSING)
            }
        }.launchIn(CoroutineScope(Dispatchers.Default))

        playgroundManager.state.debounce(20).onEach { state ->
            println(state.animationState)
            redrawPlayground()
        }.launchIn(CoroutineScope(Dispatchers.Default))

    }

    fun SContainer.redrawPlayground(){
        playgroundView?.let {
            it.removeFromParent()
        }
        playgroundView = container {
            playgroundManager.iterateBlocks { col, row, block ->
                playgroundBlock(
                    col = col,
                    row = row,
                    power = block.power,
                    animationState = playgroundManager.state.value.playgroundBlocksAnimatingState[block.id]!!
                        .animatingState!!,
                    targetPower = block.targetPower,
                    collapsingState = block.collapsingState,
                    movingState = block.movingState,
                    playgroundAnimationState = playgroundManager.state.value.animationState,
                    onNewBlockAnimationFinished = {
                        onCollapseBlockAnimationFinishedFlag.value = true
                        playgroundManager.setAnimationState(AnimationState.BLOCKS_COLLAPSING)
                        playgroundManager.updatePlaygroundAnimationState()
                    },
                    onCollapseBlockAnimationFinished = {},
                    onMoveBlockAnimationFinished = {

                    }

                )
            }
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
    }
}
