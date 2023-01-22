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
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.interpolation.*
import domain.*
import kotlinx.coroutines.flow.*


class PlayScene(val playgroundManager: PlaygroundManager) : Scene() {
    val cellSize = 50
    override suspend fun SContainer.sceneMain() {
        redrawPlayground()
        playgroundManager.state.collect { state ->
            redrawPlayground()
        }


    }

    fun SContainer.redrawPlayground(){
        container {
            playgroundManager.iterateBlocks { col, row, block ->
                playgroundBlock(
                    col = col,
                    row = row,
                    power = block.power,
                    animationState = playgroundManager.state.value.playgroundBlocksAnimatingState[block.id]!!
                        .animatingState!!,
                    playgroundAnimationState = playgroundManager.state.value.animationState,
                    onCollapseBlockAnimationFinished = {},
                    onMoveBlockAnimationFinished = {
                        playgroundManager.setAnimationState(AnimationState.BLOCKS_COLLAPSING)
                    },
                    onNewBlockAnimationFinished = {}
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
        }
    }
}
