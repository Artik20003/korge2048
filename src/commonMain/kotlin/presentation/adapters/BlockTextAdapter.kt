package presentation.adapters
import kotlin.math.*

object BlockTextAdapter {

    val powerTexts: MutableMap<Int, String> = mutableMapOf()

    init {
        initPowerTexts()
    }

    fun getTextByPower(power: Int): String {
        return powerTexts.getOrElse(power) { generateTextByPower(power) }
    }

    private fun initPowerTexts() {
        for (power in 1..63) {
            generateTextByPower(power)
        }
    }

    private fun savePowerText(power: Int, text: String) {
        if (power !in powerTexts) {
            powerTexts[power] = text
        }
    }

    private fun generateTextByPower(power: Int): String {
        var powerString = (2.0.pow(power)).toLong().toString()

        if (powerString.length <= 4) {
            return powerString
        }

        val m = powerString.length % 3
        val kTimes = ((powerString.length + (3 - m) % 3) / 3) - 1

        val resSymbol: String = if (kTimes == 1) "K" else (kTimes + 63).toChar().toString()
        powerString = powerString.substring(0, powerString.length - 3 * kTimes) + resSymbol

        savePowerText(power, powerString)
        return powerString
    }

    fun getFontSizeByPower(power: Int): Double {
        return when(power) {
            in 1..9 -> 60.0
            in 10..13 -> 40.0
            else -> when (power % 10) {
                7, 8, 9 -> 40.0
                else -> 60.0
            }
        }
    }
}
