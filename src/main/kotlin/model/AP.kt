package model

import com.google.gson.annotations.SerializedName

data class AP(
    val x: Double, //Não vai ser serializado --> ou seja, o valor este valor nao vai aparecer na linha de comandos
   val y: Double
)