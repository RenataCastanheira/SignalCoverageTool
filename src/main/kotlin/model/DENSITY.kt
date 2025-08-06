package model

import com.google.gson.annotations.SerializedName

class DENSITY(
    val x: Int,
    val y: Int,
    val subRegionArea: AREA,
    var adjacencyRegionsIndexes: List<Int>? = null,
    val value: Double
) {
    override fun toString(): String {
        return "DENSITY(x=$x, y=$y, area=$subRegionArea, value=$value)"
    }
}


