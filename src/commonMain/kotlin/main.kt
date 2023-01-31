
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import presentation.*

suspend fun main() = Korge(
    width = 500, height = 600,
    virtualWidth = 500, virtualHeight = 600, bgcolor = Colors["#2b2b2b"],
    scaleAnchor = Anchor.CENTER
) {
    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ PlayScene() })
}
