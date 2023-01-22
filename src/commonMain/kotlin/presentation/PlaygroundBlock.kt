package presentation

import Constants
import com.soywiz.korge.animate.*
import com.soywiz.korge.animate.tween
import com.soywiz.korge.input.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.tween.*
import com.soywiz.korim.color.*
import com.soywiz.korio.async.*
import domain.*
import kotlinx.coroutines.*
import kotlin.math.*

fun Container.playgroundBlock(
    col: Int,
    row: Int,
    power: Int,
    animationState: PlayBlockAnimationState,
    playgroundAnimationState: AnimationState,
    targetPower: Int? = null,
    collapsingState: PlaygroundBlock.ChangingState? = null,
    movingState: PlaygroundBlock.ChangingState? = null,
    onNewBlockAnimationFinished: () -> Unit,
    onCollapseBlockAnimationFinished: () -> Unit,
    onMoveBlockAnimationFinished: () -> Unit
) =
    UIPlaygroundBlock(
        col = col,
        row = row,
        power = power,
        animationState = animationState,
        playgroundAnimationState = playgroundAnimationState,
        targetPower = targetPower,
        collapsingState = collapsingState,
        movingState = movingState,
        onNewBlockAnimationFinished = onNewBlockAnimationFinished,
        onCollapseBlockAnimationFinished = onCollapseBlockAnimationFinished,
        onMoveBlockAnimationFinished = onMoveBlockAnimationFinished,
    ).addTo(this)

class UIPlaygroundBlock(
    val col: Int,
    val row: Int,
    val power: Int,
    val animationState: PlayBlockAnimationState,
    val playgroundAnimationState: AnimationState,
    val targetPower: Int? = null,
    val collapsingState: PlaygroundBlock.ChangingState? = null,
    val movingState: PlaygroundBlock.ChangingState? = null,
    val onNewBlockAnimationFinished: () -> Unit,
    val onCollapseBlockAnimationFinished: () -> Unit,
    val onMoveBlockAnimationFinished: () -> Unit
) : Container() {
    val blockSize: Double = 50.0
    val playgroundBlock = this
    init {
        val defaultOffsetY = (row * blockSize)
        position(
            x = getXPosition(animationState),
            y = getYPosition(animationState)
        )
            container {
                block(power)
                text(animationState.toString())
            }

        if(animationState == PlayBlockAnimationState.BOTTOM) {
            launchImmediately( Dispatchers.Default) {
               animate {
                   parallel {
                       moveTo(
                           view = playgroundBlock,
                           x = getXPosition(PlayBlockAnimationState.PLACED),
                           y = getYPosition(PlayBlockAnimationState.PLACED),
                       )

                   }
                   block {
                       onNewBlockAnimationFinished()
                   }
               }
            }

        }

    }

    private fun getYPosition(animationState: PlayBlockAnimationState): Double{
        return when(animationState) {
            PlayBlockAnimationState.BOTTOM -> Constants.Playground.ROW_COUNT * blockSize
            PlayBlockAnimationState.COLLAPSED -> (collapsingState?.targetRow ?: row) * blockSize
            PlayBlockAnimationState.MOVED -> {
                (movingState?.targetRow ?: row) * blockSize
            }

            else -> row * blockSize
        }
    }

    private fun getXPosition(animationState: PlayBlockAnimationState): Double {
        return when(animationState){
            PlayBlockAnimationState.COLLAPSED -> (collapsingState?.targetCol ?: col)  * blockSize
            PlayBlockAnimationState.MOVED -> (movingState?.targetCol ?: col)  * blockSize
            else -> col * blockSize
        }
    }


}


