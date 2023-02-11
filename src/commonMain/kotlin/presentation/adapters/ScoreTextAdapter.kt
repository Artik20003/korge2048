package presentation.adapters

import com.soywiz.kbignum.*

object ScoreTextAdapter {

    fun getTextByScore(score: BigInt): String {
        return BlockTextAdapter.generateTextByStringValue(score.toString())
    }
}
