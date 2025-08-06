package model

import com.google.gson.annotations.SerializedName

data class AREA (
    val width: Int,
    val height: Int
){
    override fun toString(): String {
        return "AREA(width=$width, height=$height)"
    }
}

