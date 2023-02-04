
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.service.storage.*
import com.soywiz.korim.font.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import data.*
import presentation.*

suspend fun main() = Korge(
    width = Constants.UI.WIDTH,
    height = Constants.UI.HEIGHT,
    virtualWidth = Constants.UI.WIDTH,
    virtualHeight = Constants.UI.HEIGHT,
    bgcolor = StyledColors.theme.mainBg,
    scaleAnchor = Anchor.CENTER,
    clipBorders = false

) {
    DefaultFontFamily.font = TtfFont(resourcesVfs["Itim-Regular.ttf"].readBytes())
    DefaultStorage.storage = views.storage

    val sceneContainer = sceneContainer()

    sceneContainer.changeTo({ PlayScene() })
}
