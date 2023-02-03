
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import presentation.*

suspend fun main() = Korge(
    width = Constants.UI.WIDTH,
    height = Constants.UI.HEIGHT,
    virtualWidth = Constants.UI.WIDTH,
    virtualHeight = Constants.UI.HEIGHT,
    bgcolor = Colors["#2b2b2b"],
    scaleAnchor = Anchor.CENTER,
    clipBorders = false

) {
    DefaultFontFamily.font = TtfFont(resourcesVfs["Exo2-VariableFont_wght.ttf"].readBytes())
    DefaultFontFamily.font = TtfFont(resourcesVfs["Itim-Regular.ttf"].readBytes())
    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ PlayScene() })
}
