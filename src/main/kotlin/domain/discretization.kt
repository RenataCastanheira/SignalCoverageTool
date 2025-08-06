package org.example.domain

import domain.whichRegionDoesThePointBelong
import model.AP_TARGET
import model.AREA
import model.COORDINATES
import model.DENSITY
import org.example.domain.propagation.atenuationValues
import org.example.domain.propagation.buildPropagationTree
import org.example.domain.propagation.findAllWallMidpointAttenuation
import org.example.domain.propagation.findLowestAttenuation
import org.example.domain.propagation.findLowestAttenuationFromMidpoints
import org.example.domain.propagation.findLowestAttenuationSorted
import java.io.File

//NOTA : ESTE VALOR DEVE SER DADO PELO UTILIZADOR
object Config{
    var L = 100
    var dRef = 100
    var cumulatedWallLoss= 10
    var n= 2 //expoente de perda
    var atenInDistRef= 40
}

 // trabalhamos em cm e não em metros
val targetPoints = mutableListOf<COORDINATES>()


/*
normalmente as coordenadas iniciais são: (0,0)
 */

fun discretization (initialCoordinates: COORDINATES, buildingArea: AREA){
    var x = initialCoordinates.x
    var y = initialCoordinates.y

    while (x <= buildingArea.width ) {

        while (y <= buildingArea.height) {

            targetPoints.add(COORDINATES(x, y))
            y += Config.L
        }

        y = initialCoordinates.y
        x += Config.L

    }

}


fun buildCoverageReport(
    allSubregions: List<DENSITY>,
    ApCoordinates: List<COORDINATES>,
    buildingArea: AREA,
    outputFileName: String = "coverageReport.txt"
): List<Pair<AP_TARGET, Double>> {
    // Gera os pontos discretizados da área
    targetPoints.clear()
    discretization(COORDINATES(0, 0), buildingArea)

    //Lista com todos os pares ap-target -> atenuação
    val resultList = mutableListOf<Pair<AP_TARGET, Double>>()

    //limpa o ficheiro se já existir
    val outputFile = File(outputFileName)
    outputFile.writeText("") // apaga o conteúdo anterior, se existir

    // Para cada AP
    for (ap in ApCoordinates) {
        //outputFile.appendText("AP(${ap.x}, ${ap.y}):")

        // encontrar a sub-região mais próxima do ap
        val sourceRegion = whichRegionDoesThePointBelong(ap,allSubregions)//corrigir
        val sourceRegionIndex = allSubregions.indexOf(sourceRegion)

        for (target in targetPoints) {
            // Ignora se AP e Target forem a mesma coordenada
            if (ap == target) continue

            // encontrar a sub-região mais próxima do target
            val destRegion = whichRegionDoesThePointBelong(target,allSubregions)//corrigir
            val destRegionIndex = allSubregions.indexOf(destRegion)

            if (destRegionIndex == null) continue

            val attenuation = findLowestAttenuation(
                current = sourceRegionIndex,
                destiny = destRegionIndex,
                allSubregions = allSubregions,
                apOrigin = ap,
                targetPoint = target
            )

            //grava o output no ficheiro
            outputFile.appendText(" AP(${ap.x}, ${ap.y})-> Target( ${target.x}, ${target.y} )-> Attenuation = %.2f dB\n".format(attenuation))
        }
        //outputFile.appendText("\n")

    }

    println("Relatório gravado no ficheiro : $outputFileName")
    return resultList
}

fun buildCoverageReportSorted(
    allSubregions: List<DENSITY>,
    ApCoordinates: List<COORDINATES>,
    buildingArea: AREA,
    outputFileName: String = "coverageReport.txt"
): List<Pair<AP_TARGET, Double>> {
    // Gera os pontos discretizados da área
    targetPoints.clear()
    discretization(COORDINATES(0, 0), buildingArea)

    //Lista com todos os pares ap-target -> atenuação
    val resultList = mutableListOf<Pair<AP_TARGET, Double>>()

    //limpa o ficheiro se já existir
    val outputFile = File(outputFileName)
    outputFile.writeText("") // apaga o conteúdo anterior, se existir

    // Para cada AP
    for (ap in ApCoordinates) {
        //outputFile.appendText("AP(${ap.x}, ${ap.y}):")

        // encontrar a sub-região mais próxima do ap
        val sourceRegion = whichRegionDoesThePointBelong(ap,allSubregions)//corrigir
        val sourceRegionIndex = allSubregions.indexOf(sourceRegion)

        for (target in targetPoints) {
            // Ignora se AP e Target forem a mesma coordenada
            if (ap == target) continue

            // encontrar a sub-região mais próxima do target
            val destRegion = whichRegionDoesThePointBelong(target,allSubregions)//corrigir
            val destRegionIndex = allSubregions.indexOf(destRegion)

            if (destRegionIndex == null) continue

            val attenuation = findLowestAttenuationSorted(
                current = sourceRegionIndex,
                destiny = destRegionIndex,
                allSubregions = allSubregions,
                apOrigin = ap,
                targetPoint = target
            )

            //grava o output no ficheiro
            outputFile.appendText(" AP(${ap.x}, ${ap.y})-> Target( ${target.x}, ${target.y} )-> Attenuation = %.2f dB\n".format(attenuation))
        }
        //outputFile.appendText("\n")

    }

    println("Relatório gravado no ficheiro : $outputFileName")
    return resultList
}

fun buildCoverageReportv2(
    allSubregions: List<DENSITY>,
    ApCoordinates: List<COORDINATES>,
    buildingArea: AREA,
    outputFileName: String = "coverageReport.txt"
): List<Pair<AP_TARGET, Double>> {
    // Gera os pontos discretizados da área
    targetPoints.clear()
    discretization(COORDINATES(0, 0), buildingArea)

    //Lista com todos os pares ap-target -> atenuação
    val resultList = mutableListOf<Pair<AP_TARGET, Double>>()

    //limpa o ficheiro se já existir
    val outputFile = File(outputFileName)
    outputFile.writeText("") // apaga o conteúdo anterior, se existir

    // Calcula a atenuação nos midpoints das paredes entre regiões
    val midpointAttenuation = findAllWallMidpointAttenuation(allSubregions)

    // Para cada AP
    for (ap in ApCoordinates) {
        //outputFile.appendText("AP(${ap.x}, ${ap.y}):")

        for (target in targetPoints) {
            // Ignora se AP e Target forem a mesma coordenada
            if (ap == target) continue

            val attenuation = findLowestAttenuationFromMidpoints(
                allSubregions,
                midpointAttenuation,
                ap,
                target
            )

            //grava o output no ficheiro
            outputFile.appendText(" AP(${ap.x}, ${ap.y})-> Target( ${target.x}, ${target.y} )-> Attenuation = %.2f dB\n".format(attenuation.totalAttenuation))

        }
        //outputFile.appendText("\n")

    }

    println("Relatório gravado no ficheiro : $outputFileName")
    return resultList
}
