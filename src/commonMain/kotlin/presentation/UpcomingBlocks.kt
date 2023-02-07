package presentation

import Constants
import com.soywiz.klock.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.async.*
import kotlinx.coroutines.*
import presentation.adapters.*

class UpcomingBlocks() : Container() {
    private var alignUnderContainer: Container? = null
    private var upcomingBlocks: MutableList<Block?> = mutableListOf()
    private var firstUpcomingValueRect: Container? = null
    private var secondUpcomingValueRect: Container? = null
    private val secondBlockScale = .75
    private val animationTime = Constants.Playground.ANIMATION_TIME
    val strokeThickness = SizeAdapter.columnSize * .05

    fun setAlignUnderContainer(container: Container) {
        alignUnderContainer = container
    }
    fun drawUpcomingValues(
        firstValue: Int,
        secondValue: Int,

    ) {
        drawUpcomingRects()
        if (upcomingBlocks.isNotEmpty())
            return

        upcomingBlocks.add(
            block(
                power = firstValue,
                cellSize = SizeAdapter.cellSize,
            ).also {
                it.centerOn(firstUpcomingValueRect!!)
            }
        )
        upcomingBlocks.add(
            block(
                power = secondValue,
                cellSize = SizeAdapter.cellSize,
            ).also {
                it.also { it.scale(secondBlockScale) }.centerOn(secondUpcomingValueRect!!)
            }
        )
    }

    private fun clearValues() {
        upcomingBlocks.map { it?.removeFromParent() }
        upcomingBlocks = mutableListOf()
    }

    private fun drawUpcomingRects() {
        if (firstUpcomingValueRect != null)
            return

        if (secondUpcomingValueRect != null)
            return

        container {

            firstUpcomingValueRect = upcomingRect().also {
                it.alignTopToBottomOf(alignUnderContainer ?: containerRoot, 10)
                it.centerXOn(alignUnderContainer ?: containerRoot)
            }

            secondUpcomingValueRect = upcomingRect().also {
                it.alignLeftToRightOf(firstUpcomingValueRect!!, 10).scale(secondBlockScale)
                it.alignTopToBottomOf(alignUnderContainer ?: containerRoot, 10)
            }
        }.positionX(-strokeThickness / 2).alignTopToBottomOf(alignUnderContainer!!, padding = 30)
    }

    fun rotateValues(firstValue: Int, secondValue: Int) {
        if (firstValue != upcomingBlocks[0]!!.power) {
            clearValues()
            drawUpcomingValues(
                firstValue = firstValue,
                secondValue = secondValue
            )
            return
        }

        hideFirstBlock()
        moveSecondBlock()
        showHiddenBlock(secondValue)
    }

    private fun hideFirstBlock() {
        launchImmediately(Dispatchers.Default) {
            upcomingBlocks.getOrNull(0)?.let { firstBlock ->
                firstBlock.animate {
                    parallel {
                        hide(
                            view = firstBlock,
                            time = TimeSpan(animationTime / 2)
                        )
                        block { firstBlock.removeFromParent() }
                    }
                }
            }
        }
    }

    private fun moveSecondBlock() {

        val firstBlock = upcomingBlocks[0]!!
        val secondBlock = upcomingBlocks[1]!!

        val firstBlockInitialX = firstBlock.x
        val firstBlockInitialY = firstBlock.y

        launchImmediately(Dispatchers.Default) {

            secondBlock.animate {
                parallel {
                    moveTo(
                        view = secondBlock,
                        x = firstBlockInitialX,
                        y = firstBlockInitialY,
                        time = TimeSpan(animationTime)
                    )
                    scaleTo(
                        view = secondBlock,
                        scaleX = 1,
                        time = TimeSpan(animationTime)
                    )
                }
            }
        }
    }

    private fun showHiddenBlock(newValue: Int) {

        val secondBlock = upcomingBlocks[1]!!
        val secondBlockInitialX = secondBlock.x
        val secondBlockInitialY = secondBlock.y
        val secondBlockInitialSize = secondBlock.scaledHeight

        val hiddenBlock = block(
            power = newValue,
            cellSize = SizeAdapter.cellSize,
        ).also {
            it.x = secondBlockInitialX
            it.y = secondBlockInitialY + secondBlockInitialSize
            it.scale(0)
            it.centerOn(secondUpcomingValueRect!!)
        }
        launchImmediately(Dispatchers.Default) {
            hiddenBlock.animate {
                parallel {

                    moveTo(
                        view = hiddenBlock,
                        x = secondBlockInitialX,
                        y = secondBlockInitialY,
                        time = TimeSpan(animationTime)
                    )
                    scaleBy(
                        view = hiddenBlock,
                        scaleX = secondBlockScale
                    )
                }
                block {
                    upcomingBlocks[0] = secondBlock
                    upcomingBlocks[1] = hiddenBlock
                }
            }
        }
    }

    fun Container.upcomingRect() = RoundRect(
        width = SizeAdapter.columnSize,
        height = SizeAdapter.columnSize,
        rx = SizeAdapter.columnSize * .17,
        stroke = Colors.WHITE,
        fill = Colors.TRANSPARENT_WHITE,
        strokeThickness = strokeThickness
    ).addTo(this)
}
