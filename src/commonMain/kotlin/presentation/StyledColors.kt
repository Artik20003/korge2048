package presentation

import com.soywiz.korim.color.*

object StyledColors {
    var themeType: UITheme = UITheme.DEFAULT

    val theme: ColorTheme
        get() {
            return when (themeType) {
                UITheme.DEFAULT -> DefaultColorTheme()
            }
        }
}

enum class UITheme {
    DEFAULT,
}

interface ColorTheme {
    val mainBg: RGBA
    val playgroundColumnBg: RGBA
}

class DefaultColorTheme(
    override val mainBg: RGBA = Colors["#2b2b2b"],
    override val playgroundColumnBg: RGBA = RGBA(r = 100, g = 100, b = 100, a = 30)

) : ColorTheme
