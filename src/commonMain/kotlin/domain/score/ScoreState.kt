package domain.score

import com.soywiz.kbignum.*

data class ScoreState(
    var score: BigInt = BigInt.ZERO,
    var bestScore: BigInt = BigInt.ZERO,

)
