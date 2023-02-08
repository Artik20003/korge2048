package presentation.adapters

object ScoreTextAdapter {

    fun getTextByScore(score: Int): String {
        return BlockTextAdapter.generateTextByStringValue(score.toString())
    }
}
