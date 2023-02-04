package presentation.adapters

import Constants
import kotlin.math.*

object SizeAdapter {
    private const val horizontalPlaygroundMarginPercent = .05
    private const val horizontalPlaygroundColumnMarginPercent = .01

    val horizontalPlaygroundMarginValue
        get() = Constants.UI.WIDTH * horizontalPlaygroundMarginPercent

    val cellSize: Double
        get() = columnSize - horizontalPlaygroundColumnMarginValue * 2

    val horizontalPlaygroundColumnMarginValue
        get() = ceil(columnSize * horizontalPlaygroundColumnMarginPercent)

    val columnSize: Double =
        (Constants.UI.WIDTH - horizontalPlaygroundMarginValue * 2.0) / Constants.Playground.COL_COUNT
}
