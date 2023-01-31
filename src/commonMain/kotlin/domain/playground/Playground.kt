package domain.playground

data class Playground(
    val blocks: List<MutableList<PlaygroundBlock>> =
        List<MutableList<PlaygroundBlock>>(5) { mutableListOf() },
) {
    fun iterateBlocks(handler: (col: Int, row: Int, block: PlaygroundBlock) -> Unit) {
        for (col in 0 until Constants.Playground.COL_COUNT) {
            blocks[col].forEachIndexed { row, block ->
                handler(col, row, block)
            }
        }
    }
}
