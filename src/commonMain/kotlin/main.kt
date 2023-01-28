import com.soywiz.klock.*
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.interpolation.*
import domain.*
import presentation.*

suspend fun main() = Korge(width = 500, height = 600,
    virtualWidth = 250, virtualHeight = 300,  bgcolor = Colors["#2b2b2b"],
    scaleAnchor = Anchor.CENTER
) {
	val sceneContainer = sceneContainer()
    val playgroundManager = PlaygroundManager()
	sceneContainer.changeTo({ PlayScene(playgroundManager) })
}

