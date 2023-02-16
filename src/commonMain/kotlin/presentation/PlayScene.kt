package presentation

import Constants
import com.soywiz.klock.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.service.storage.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korim.paint.*
import com.soywiz.korim.text.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import com.soywiz.korio.stream.*
import com.soywiz.korio.util.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import com.soywiz.korma.interpolation.*
import data.*
import domain.*
import domain.level.*
import domain.playground.*
import domain.score.*
import domain.upcoming.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import presentation.adapters.*
import presentation.popup.*

@OptIn(FlowPreview::class)
class PlayScene() : Scene() {

    val playgroundManager: PlaygroundManager = PlaygroundManager()
    val levelManager: LevelManager = LevelManager(playgroundManager.state.value.playground)
    val scoreManager: ScoreManager = ScoreManager(
        playgroundManager.state.value.playground, DefaultStorage.storage
    )
    val upcomingValuesManager: UpcomingValuesManager = UpcomingValuesManager()
    var onNewBlockAnimationFinishedFlag = MutableStateFlow(false)
    var onCollapseBlockAnimationFinishedFlag = MutableStateFlow(false)
    var onMoveBlockAnimationFinishedFlag = MutableStateFlow(false)
    var onRemoveBlockAnimationFinishedFlag = MutableStateFlow(false)
    var blocks: MutableMap<UUID, UIPlaygroundBlock> = mutableMapOf()
    var playground: Container? = null
    var playgroundBgColumns: Container? = null
    var topBar: Container? = null
    var upcomingBlocks = UpcomingBlocks()

    override suspend fun SContainer.sceneMain() {
        container {

            text(playgroundManager.state.value.animationState.toString()) {
                playgroundManager.state.onEach {
                    text = it.animationState.toString()
                }.launchIn(CoroutineScope(Dispatchers.Default))
            }

            topBar = fixedSizeContainer(
                width = Constants.UI.WIDTH,
                height = 100,
            ) {

                // Current Score
                text(
                    text = ScoreTextAdapter.getTextByScore(scoreManager.state.value.score),
                    textSize = 60.0,
                    font = DefaultFontFamily.font,
                ) {
                    scoreManager.state.onEach {
                        text = ScoreTextAdapter.getTextByScore(it.score)
                        centerOn(this.parent ?: this.containerRoot)
                    }.launchIn(CoroutineScope(Dispatchers.Default))
                }
            }
            // Best Score
            container {
                val bestScoreContainer = this
                val crownIcon = image(texture = resourcesVfs["/icons/crown.svg"].readSVG().render()) {
                    scale = .02
                }

                text(
                    text = ScoreTextAdapter.getTextByScore(scoreManager.state.value.bestScore),
                    textSize = 35.0,
                ) {
                    alignLeftToRightOf(crownIcon, 7.0)
                    positionY(13)
                    scoreManager.state.onEach {
                        text = ScoreTextAdapter.getTextByScore(it.bestScore)
                        alignLeftToRightOf(crownIcon, 6.0)
                        bestScoreContainer.alignRightToRightOf(topBar!!, 15)
                    }.launchIn(CoroutineScope(Dispatchers.Default))
                }
                alignRightToRightOf(topBar!!, 15)
                alignTopToTopOf(topBar!!, 5)
            }

            playgroundBgColumns = drawBgColumns()

            setOnEndAnimationHandlers()
            playgroundManager.addOnStaticStateListener {
                levelManager.playground = playgroundManager.state.value.playground
                levelManager.upgradeLevelIfNeeded()
                playgroundManager.checkEndOfGame(upcomingValuesManager.state.value.upcomingValues[0])
            }

            playgroundManager.addOnCollapsedStateListener {
                scoreManager.playground = playgroundManager.state.value.playground
                scoreManager.updateScore()
            }
            // show Cascade
            playgroundManager.addOnCascadeListener { cascadeCount ->
                launchImmediately {
                    showWowCascadeContainer(cascadeCount)
                }
            }

            playgroundManager.addOnEndOfGameListener {
                outOfMovesPopup()
            }

            // !!TODO launch only if animationState changed
            playgroundManager.state.debounce(20).onEach { state ->

                updateUIBlockState()
                when (state.animationState) {
                    AnimationState.NEW_BLOCK_PLACING,
                    AnimationState.STATIC,
                    -> redrawPlayground()

                    AnimationState.BLOCKS_COLLAPSING -> {
                        blocks.forEach { it.value.collapseIfNeeded() }
                    }

                    AnimationState.BLOCKS_MOVING -> {
                        blocks.forEach { it.value.moveIfNeeded() }
                    }

                    AnimationState.BLOCKS_REMOVING -> {
                        redrawPlayground()
                        blocks.forEach { it.value.removeIfNeeded() }
                    }
                }
            }.launchIn(CoroutineScope(Dispatchers.Default))

            levelManager.state.onEach {
                println("Setting new min upcoming value: ${it.level}")
                upcomingValuesManager.updateLevelUpcomingValues(it.level)
                playgroundManager.removeBlocksByMinPower(it.level)
            }.launchIn(CoroutineScope(Dispatchers.Default))

            // upcomingValues UI
            val upcomingValues = upcomingValuesManager.state.value.upcomingValues

            upcomingBlocks.setAlignUnderContainer(playgroundBgColumns!!)
            upcomingBlocks.addTo(this)
            upcomingBlocks.drawUpcomingValues(
                firstValue = upcomingValues[0],
                secondValue = upcomingValues[1],
            )

            upcomingValuesManager.addOnGenerateUpcomingValuesListener {
                upcomingBlocks.rotateValues(
                    firstValue = upcomingValuesManager.state.value.upcomingValues[0],
                    secondValue = upcomingValuesManager.state.value.upcomingValues[1]
                )
            }
        }
    }

    private suspend fun Container.showWowCascadeContainer(cascadeCount: Int) {
        val wowText: String? = when (cascadeCount) {
            3 -> "WOW!"
            4 -> "PERFECT!"
            5 -> "BRILLIANT!"
            6 -> "OMG!"
            7 -> "CHUCK NORRIS!"
            else -> null
        }
        wowText?.let {
            val wowContainer = container {
                centerOn(playground ?: containerRoot)
                zIndex = 100.0
            }

            val spriteMap = resourcesVfs["sprites/fireworks.png"].readBitmap()
            val explosionAnimation = SpriteAnimation(
                spriteMap = spriteMap,
                spriteWidth = 300,
                spriteHeight = 300,
                marginTop = 0,
                marginLeft = 0,
                columns = 1,
                rows = 50,
                offsetBetweenColumns = 0,
                offsetBetweenRows = 0,
            )

            val explosion = Sprite(explosionAnimation)
                .centerOn(wowContainer)
                .addTo(wowContainer)
            val text = Text(
                text = wowText,
                textSize = SizeAdapter.cellSize / 1.5,
                font = DefaultFontFamily.font
            ).centerOn(wowContainer).addTo(wowContainer)

            animate {
                parallel {
                    explosion.playAnimation()

                    scaleTo(
                        view = wowContainer,
                        scaleX = 1.25,
                        time = TimeSpan(800.0)
                    )

                    hide(
                        view = text,
                        time = TimeSpan(1000.0),
                        easing = Easing.EASE_IN_OUT
                    )
                }
                block {
                    wowContainer.removeFromParent()
                }
            }
        }
    }

    private fun Container.drawBgColumns(): Container {
        return container {
            alignTopToBottomOf(topBar ?: containerRoot)
            positionX(SizeAdapter.horizontalPlaygroundMarginValue)

            for (i in 0 until Constants.Playground.COL_COUNT) {
                container {
                    solidRect(
                        width = SizeAdapter.cellSize,
                        height = Constants.Playground.ROW_COUNT * SizeAdapter.cellSize,
                        color = StyledColors.theme.playgroundColumnBg
                    ).position(
                        x = SizeAdapter.horizontalPlaygroundColumnMarginValue,
                        y = 0.0
                    )
                }.position(
                    x = i * SizeAdapter.columnSize,
                    y = 0.0
                )
            }
        }
    }

    private fun setOnEndAnimationHandlers() {

        onNewBlockAnimationFinishedFlag.debounce(100).onEach { flag ->
            if (flag) {
                playgroundManager.setAnimationState(AnimationState.BLOCKS_COLLAPSING)
                onNewBlockAnimationFinishedFlag.value = false
            }
        }.launchIn(CoroutineScope(Dispatchers.Default))

        onCollapseBlockAnimationFinishedFlag.debounce(100).onEach { flag ->
            if (flag) {
                playgroundManager.setAnimationState(AnimationState.BLOCKS_MOVING)
                onCollapseBlockAnimationFinishedFlag.value = false
            }
        }.launchIn(CoroutineScope(Dispatchers.Default))

        onMoveBlockAnimationFinishedFlag.debounce(100).onEach { flag ->
            if (flag) {
                playgroundManager.setAnimationState(AnimationState.STATIC)
                onMoveBlockAnimationFinishedFlag.value = false
            }
        }.launchIn(CoroutineScope(Dispatchers.Default))

        onRemoveBlockAnimationFinishedFlag.debounce(100).onEach { flag ->
            if (flag) {
                playgroundManager.setAnimationState(AnimationState.BLOCKS_MOVING)
                onRemoveBlockAnimationFinishedFlag.value = false
            }
        }.launchIn(CoroutineScope(Dispatchers.Default))
    }

    fun Container.redrawPlayground() {
        zIndex(10)
        blocks = mutableMapOf()
        val newPlayground = container {
            alignTopToBottomOf(topBar ?: containerRoot)
            this.positionX(SizeAdapter.horizontalPlaygroundMarginValue)
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
                    removingState = block.removingState,
                    playgroundAnimationState = playgroundManager.state.value.animationState,
                    onNewBlockAnimationFinished = { onNewBlockAnimationFinishedFlag.value = true },
                    onCollapseBlockAnimationFinished = { onCollapseBlockAnimationFinishedFlag.value = true },
                    onMoveBlockAnimationFinished = { onMoveBlockAnimationFinishedFlag.value = true },
                    onRemoveBlockAnimationFinished = { onRemoveBlockAnimationFinishedFlag.value = true }
                )
                blocks[block.id] = playgroundBlock
            }
            for (colNum in 0 until Constants.Playground.COL_COUNT) {
                clickableColumn(
                    onClick = {
                        playgroundManager.push(colNum, upcomingValuesManager.state.value.upcomingValues[0]) {
                            upcomingValuesManager.generateUpcomingValues()
                        }
                    }
                )
                    .position(
                        x = (colNum * SizeAdapter.columnSize).toInt(),
                        y = 0
                    )
            }
        }
        playground?.removeFromParent()
        playground = newPlayground
    }

    fun updateUIBlockState() {

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
