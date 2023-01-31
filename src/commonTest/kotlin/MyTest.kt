
import com.soywiz.korge.tests.*
import domain.playground.*
import presentation.*
import presentation.adapters.*
import kotlin.test.*

class MyTest : ViewsForTesting() {

    @Test
    fun testBlockText() = viewsTest {
        val testBlock = playgroundBlock(
            col = 0,
            row = 0,
            power = 15,
            playgroundAnimationState = AnimationState.STATIC,
            animationState = PlayBlockAnimationState.PLACED,
            onCollapseBlockAnimationFinished = {},
            onMoveBlockAnimationFinished = {},
            onNewBlockAnimationFinished = {}
        )

        assertEquals("32K", BlockTextAdapter.getTextByPower(testBlock.power))
    }

    /*
    @Test
    fun test() = viewsTest {
        val log = arrayListOf<String>()
        val rect = solidRect(100, 100, Colors.RED)
        rect.onClick {
            log += "clicked"
        }
        assertEquals(1, views.stage.numChildren)
        rect.simulateClick()
        assertEquals(true, rect.isVisibleToUser())
        tween(rect::x[-102], time = 10.seconds)
        assertEquals(Rectangle(x = -102, y = 0, width = 100, height = 100), rect.globalBounds)
        assertEquals(false, rect.isVisibleToUser())
        assertEquals(listOf("clicked"), log)
    }

     */
}
