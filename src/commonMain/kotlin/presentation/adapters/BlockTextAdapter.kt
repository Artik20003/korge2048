package presentation.adapters

import kotlin.math.*

object BlockTextAdapter {
    fun getTextByPower(power: Int): String {
        var powerString = (2.0.pow(power)).toInt().toString()

        if (powerString.length <= 4) {
            return powerString
        }

        val m = powerString.length % 3
        val kTimes = ((powerString.length + (3 - m) % 3) / 3) - 1

        var resSymbol: String = if (kTimes == 1) "K" else (kTimes + 63).toChar().toString()

        powerString = powerString.substring(0, powerString.length - 3 *kTimes) + resSymbol

        return powerString
    }

    fun getFontSizeByPower(power: Int): Double {
        return 25.0
    }
}
