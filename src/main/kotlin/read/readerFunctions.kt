package read

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import model.AP
import model.AREA
import model.DENSITY
import java.io.File

fun areaReader (): AREA {

    val gson = Gson()

    // Leitura dos ficheiros Json
    val areaInput = File("/Users/renatacastanheira/Documents/faculdade/projeto/projeto/src/main/resources/AREA.json").readText()

    val area: AREA = gson.fromJson(areaInput, AREA::class.java)

    // Impressão dos dados lidos
    println("Área: $area")

    return area

}

fun apsReader(): List<AP> {

    val gson = Gson()

    // Leitura dos ficheiros JSON
    val apsInput = File("/Users/renatacastanheira/Documents/faculdade/projeto/projeto/src/main/resources/AP.json").readText()

    // Descerialização dos dados
    val apsType = object : TypeToken<List<AP>>() {}.type

    val apsPositions: List<AP> = gson.fromJson(apsInput, apsType)

    // Impressão dos dados lidos
    println("APs: $apsPositions")

    return apsPositions

}


fun densitiesReader(): List<DENSITY> {

    val gson = Gson()

    // Leitura dos ficheiros JSON
    val densityInput = File("/Users/renatacastanheira/Documents/faculdade/projeto/projeto/src/main/resources/DENSITY.json").readText()

    // Descerialização dos dados
    val densityType = object : TypeToken<List<DENSITY>>() {}.type

    val densities: List<DENSITY> = gson.fromJson(densityInput, densityType)

    // Impressão dos dados lidos
    println("Densidades: ")
    densities.forEach { density -> println("x: ${density.x}")
        println("y: ${density.y}")
        println("sub region area width: ${density.subRegionArea.width}")
        println("sub region area height: ${density.subRegionArea.height}")
        println("value: ${density.value}")
    }

    return densities

}
