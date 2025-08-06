package model

data class COORDINATES (
    val x:Int,
    val y:Int
) {
     operator fun plus(coord2: COORDINATES): COORDINATES {
        return COORDINATES(x + coord2.x, y + coord2.y)
    }
}
