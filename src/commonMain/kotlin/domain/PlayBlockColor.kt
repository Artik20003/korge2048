package domain

import com.soywiz.korim.color.*


class PlayBlockColor {
    companion object {
        private val colorSet = listOf<RGBA>(
            Colors["#47847B"],
            Colors["#25534B"],
            Colors["#7EB048"],
            Colors["#50812D"],
            Colors["#6B5E99"],
            Colors["#3C1870"],
            Colors["#4A7EB2"],
            Colors["#2A5097"],
            Colors["#A77134"],
            Colors["#704219"],
            Colors["#C6659D"],
            Colors["#A72168"],
            Colors["#C97430"],
            Colors["#931C27"],
        )
        fun getColorByPower(power: Int) = colorSet[power % colorSet.size]
    }


}
