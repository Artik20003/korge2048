package presentation

import Constants
import com.soywiz.klock.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.view.*
import com.soywiz.korio.async.*
import domain.playground.*
import kotlinx.coroutines.*
import presentation.adapters.*

fun Container.playgroundBlock(
    col: Int,
    row: Int,
    power: Int,
    animationState: PlayBlockAnimationState,
    playgroundAnimationState: AnimationState,
    isHighest: Boolean = false,
    targetPower: Int? = null,
    collapsingState: PlaygroundBlock.ChangingState? = null,
    movingState: PlaygroundBlock.ChangingState? = null,
    removingState: Boolean = false,
    onNewBlockAnimationFinished: () -> Unit,
    onCollapseBlockAnimationFinished: () -> Unit,
    onMoveBlockAnimationFinished: () -> Unit,
    onRemoveBlockAnimationFinished: () -> Unit,
) =
    UIPlaygroundBlock(
        col = col,
        row = row,
        power = power,
        animationState = animationState,
        playgroundAnimationState = playgroundAnimationState,
        isHighest = isHighest,
        targetPower = targetPower,
        collapsingState = collapsingState,
        movingState = movingState,
        removingState = removingState,
        onNewBlockAnimationFinished = onNewBlockAnimationFinished,
        onCollapseBlockAnimationFinished = onCollapseBlockAnimationFinished,
        onMoveBlockAnimationFinished = onMoveBlockAnimationFinished,
        onRemoveBlockAnimationFinished = onRemoveBlockAnimationFinished,
    ).addTo(this)

class UIPlaygroundBlock(
    var col: Int,
    var row: Int,
    var power: Int,
    var animationState: PlayBlockAnimationState,
    var playgroundAnimationState: AnimationState,
    isHighest: Boolean = false,
    var targetPower: Int? = null,
    var collapsingState: PlaygroundBlock.ChangingState? = null,
    var movingState: PlaygroundBlock.ChangingState? = null,
    var removingState: Boolean = false,
    var onNewBlockAnimationFinished: () -> Unit,
    var onCollapseBlockAnimationFinished: () -> Unit,
    var onMoveBlockAnimationFinished: () -> Unit,
    var onRemoveBlockAnimationFinished: () -> Unit,
) : Container() {
    private var block: Block? = null
    var isHighest: Boolean = isHighest
        set(value) {
            if (isHighest != value) {
                field = value
                block?.let {
                    it.isHighest = value
                    it.redrawBlock()
                }
            } else
                field = value
        }
    private val cellSize: Double = SizeAdapter.cellSize
    private var playgroundBlock: UIPlaygroundBlock = this
    private val isExtraRowPlacing = row == Constants.Playground.ROW_COUNT

    init {
        position(
            x = getXPosition(animationState),
            y = getYPosition(animationState)
        )

        container {
            if (!isExtraRowPlacing) {
                block = block(power = power, cellSize = cellSize, isHighest = isHighest)
                // text(animationState.toString())
            }
        }

        if (animationState == PlayBlockAnimationState.BOTTOM) {
            launchImmediately(Dispatchers.Default) {
                animate {
                    parallel {
                        moveTo(
                            view = playgroundBlock,
                            x = getXPosition(PlayBlockAnimationState.PLACED),
                            y = getYPosition(PlayBlockAnimationState.PLACED),
                            time =
                            if (!isExtraRowPlacing)
                                TimeSpan(Constants.Playground.ANIMATION_TIME)
                            else
                                TimeSpan(0.0)
                        )
                    }
                    block {
                        onNewBlockAnimationFinished()
                    }
                }
            }
        }
    }

    private fun getYPosition(animationState: PlayBlockAnimationState): Double {
        val cellSizeWithMargin = cellSize + SizeAdapter.horizontalPlaygroundColumnMarginValue
        return when (animationState) {
            PlayBlockAnimationState.BOTTOM ->
                Constants.Playground.ROW_COUNT * cellSizeWithMargin

            PlayBlockAnimationState.COLLAPSED ->
                (collapsingState?.targetRow ?: row) * cellSizeWithMargin

            PlayBlockAnimationState.MOVED -> {
                (movingState?.targetRow ?: row) * cellSizeWithMargin
            }

            else -> row * cellSizeWithMargin
        }
    }

    private fun getXPosition(animationState: PlayBlockAnimationState): Double {
        val columnSize = SizeAdapter.columnSize
        val margin = SizeAdapter.horizontalPlaygroundColumnMarginValue
        return when (animationState) {
            PlayBlockAnimationState.COLLAPSED -> (collapsingState?.targetCol ?: col) * columnSize + margin
            PlayBlockAnimationState.MOVED -> (movingState?.targetCol ?: col) * columnSize + margin
            else -> col * columnSize + margin
        }
    }

    fun collapseIfNeeded() {
        collapsingState?.let {
            // draw current block and target on top. Then current will fade out so the target will be seen
            val newBlock = block(power = targetPower ?: power, cellSize = cellSize, isHighest = isHighest)
            val blockToVanish = block(power, cellSize = cellSize)
            block?.removeFromParent()
            block = newBlock

            launchImmediately(Dispatchers.Default) {
                animate {
                    parallel {
                        moveTo(
                            view = playgroundBlock,
                            x = getXPosition(PlayBlockAnimationState.COLLAPSED),
                            y = getYPosition(PlayBlockAnimationState.COLLAPSED),
                            time =
                            if (!isExtraRowPlacing)
                                TimeSpan(Constants.Playground.ANIMATION_TIME)
                            else
                                TimeSpan(0.0)
                        )
                        hide(
                            view = blockToVanish,
                            time = TimeSpan(Constants.Playground.ANIMATION_TIME),
                        )
                    }
                    block {
                        blockToVanish.removeFromParent()
                        if (col != it.targetCol || row != it.targetRow) {
                            playgroundBlock.removeFromParent()
                        }
                        onCollapseBlockAnimationFinished()
                    }
                }
            }
        }
    }

    fun moveIfNeeded() {
        movingState?.let {
            launchImmediately(Dispatchers.Default) {
                animate {
                    parallel {
                        moveTo(
                            view = playgroundBlock,
                            x = getXPosition(PlayBlockAnimationState.MOVED),
                            y = getYPosition(PlayBlockAnimationState.MOVED),
                            time = TimeSpan(Constants.Playground.ANIMATION_TIME),
                        )
                    }
                    block {
                        onMoveBlockAnimationFinished()
                    }
                }
            }
        }
    }

    fun removeIfNeeded() {
        if (!removingState) {
            return
        }

        launchImmediately(Dispatchers.Default) {
            playgroundBlock.animate {
                parallel {
                    hide(
                        view = playgroundBlock,
                        time = TimeSpan(Constants.Playground.ANIMATION_TIME),
                    )
                }
                block {
                    playgroundBlock.removeFromParent()
                    onRemoveBlockAnimationFinished()
                }
            }
        }
    }
}
