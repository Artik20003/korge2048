package presentation.adapters

import Constants

object CellSizeAdapter {
    private val horizontalPlaygroundMarginPercent = 10
    val horizontalPlaygroundMarginValue
        get() = Constants.UI.WIDTH * horizontalPlaygroundMarginPercent / 100

    val cellSize: Double

        get() {
            return (Constants.UI.WIDTH - horizontalPlaygroundMarginValue).toDouble() / Constants.Playground.COL_COUNT
        }
}
