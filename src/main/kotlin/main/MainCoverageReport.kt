package org.example.main

import model.AREA
import model.COORDINATES
import model.DENSITY
import org.example.domain.Config
import com.google.gson.Gson
import org.example.domain.buildCoverageReport
import org.example.domain.buildCoverageReportSorted
import org.example.domain.buildCoverageReportv2
import java.io.File
import kotlin.time.measureTime
import domain.validateData

data class InputData(
    val L: Int,
    val dRef: Int,
    val buildingArea: AREA,
    val subregions: List<DENSITY>,
    val apList: List<COORDINATES>,
    val cumulatedWallLoss: Int,
    val n: Int, //expoente de perda
    val atenInDistRef: Int
)


fun main(args: Array<String>) {
    val gson = Gson()
    val jsonInput = if (args.size == 1) File(args[0]).readText()
    else File("src/main/kotlin/main/input.json").readText()
    val inputData = gson.fromJson(jsonInput, InputData::class.java)

    // Aplica as configurações vindas do ficheiro
    Config.L = inputData.L
    Config.dRef = inputData.dRef
    Config.cumulatedWallLoss = inputData.cumulatedWallLoss
    Config.n = inputData.n
    Config.atenInDistRef = inputData.atenInDistRef

    val allSubRegions = inputData.subregions
    val allApCoordinates = inputData.apList
    val buildingArea = inputData.buildingArea
    val outputFileName = "coverageReportv2.txt"
    //val outputFileName = "coverageReportSorted.txt"
    //val outputFileName = "coverageReport.txt"

    val isValid = validateData(buildingArea, allSubRegions)
    if (isValid) {

        val timeTaken = measureTime {
            //val result = buildCoverageReport(
            //val result = buildCoverageReportSorted(
            val result = buildCoverageReportv2(
                allSubregions = inputData.subregions,
                ApCoordinates = inputData.apList,
                buildingArea = inputData.buildingArea,
                outputFileName = outputFileName
            )

            result.forEach { (apTarget, attenuation) ->
                println(
                    "AP(${apTarget.ap.x}, ${apTarget.ap.y}) -> Target(${apTarget.target.x}, ${apTarget.target.y}) = %.2f dB".format(
                        attenuation
                    )
                )
            }
            println("The coverage report was generated successfully. Check file $outputFileName for more details.")
        }
        println("Total time: $timeTaken")
    }else {

        val outputFile = File(outputFileName)

        outputFile.writeText("The coverage report wasn't generated successfully, due to invalid input data")
    }
}
