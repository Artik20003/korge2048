package domain

data class Playground (
    val blocks: List<MutableList<PlaygroundBlock>> = List<MutableList<PlaygroundBlock>>(5 ){ mutableListOf() },
)
