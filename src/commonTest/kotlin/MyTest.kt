
import com.soywiz.korge.tests.*
import kotlin.test.*
import presentation.adapters.*

class MyTest : ViewsForTesting() {

    @Test
    fun testBlockText() = viewsTest {

        for (i in 1..100) {
            println("$i: ${BlockTextAdapter.getTextByPower(i)}")
        }

        assertEquals("32K", BlockTextAdapter.getTextByPower(15))
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
