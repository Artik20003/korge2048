
import com.soywiz.korge.*
import com.soywiz.korge.bus.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.service.storage.*
import com.soywiz.korim.font.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import data.*
import kotlinx.coroutines.*
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
    DefaultFontFamily.font = TtfFont(resourcesVfs["fonts/Itim-Regular.ttf"].readBytes())
    DefaultStorage.storage = views.storage

    val sceneContainer = sceneContainer()
    val bus = GlobalBus(Dispatchers.Default)

    sceneContainer.changeTo({ PlayScene(bus = bus) })
}
