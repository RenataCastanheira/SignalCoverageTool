package org.example.domain.propagation

import domain.propagation.calculateUntilDestinyExactPoint
import domain.propagation.distanceCalculator
import domain.propagation.getWallDist
import domain.propagation.getinteractionLoss
import domain.wallMidpoint

import model.COORDINATES
import model.DENSITY
import org.example.domain.Config
import kotlin.math.log10

const val initialValue = 0

//valores constantes da seguinte formula: PL = Plo + 10 n log (d/do) + som(Lwi) + som(Lbj)
//const val cumulatedWallLoss= 10 //dB
const val aAngleFormule = 0.0556 //dB/o
//const val atenInDistRef = 40 //dB
//const val n = 2//expoente de perda
const val INF_ATTENUATION = 9999999999999.0 // dB



sealed class PropagationTree


data class Node(

    val region: Int,

    val children: List<PropagationTree>,

    val dist: Double, // nao testei

    val totalDist: Double, // nao testei

    val atenValue: Double

) : PropagationTree()



data class Leaf(val totalDist: Double, val atenValue: Double, var propValue: Double = 0.0) : PropagationTree()



fun buildPropagationTree(

    current: Int,

    destiny: Int,

    allSubregions: List<DENSITY>,

    visited: Set<Int> = emptySet(),

    parent: Int? = null,

    grandparent: Int? = null,

    parentTotalDist: Double = 0.0,

    apOrigin: COORDINATES,

    targetPoint: COORDINATES,

    propagationValue: Double = 0.0

): PropagationTree {

    val updatedVisited = visited + current


    val currentRegion = allSubregions[current]
    val parentRegion = parent?.let { allSubregions[it] }
    var grandParentRegion = grandparent?.let { allSubregions[it] }


    if (current == destiny) { // se a região atual for a de destino , então vai colocar a distância até ao ponto exato de destino

        val toDest = calculateUntilDestinyExactPoint(apOrigin,parentRegion,currentRegion,targetPoint)

        return Leaf(parentTotalDist + toDest, propagationValue)

        // Node(current, emptyList(), wallDist, totalDist + toDest, updatedPropValue)
    }

    var wallDist = 0.0
    var totalDist = 0.0
    var updatedPropValue = 0.0



    val nextIndices = currentRegion.adjacencyRegionsIndexes
        ?.filter { it !in updatedVisited }
        .orEmpty()


    val children = nextIndices.mapNotNull { neighborIndex ->

        val neighborRegion = allSubregions[neighborIndex]

        wallDist = getWallDist(parentRegion,currentRegion,neighborRegion,allSubregions,apOrigin)

        totalDist = parentTotalDist + wallDist // atualiza a distancia total

        var interactionLoss : Double? = 0.0
        if (parentRegion != null ) {
        var a: COORDINATES
            if (grandParentRegion == null) a = apOrigin
            else a = wallMidpoint(grandParentRegion, parentRegion)
            interactionLoss = getinteractionLoss(a ,parentRegion,currentRegion,neighborRegion)
        }

        updatedPropValue = propagationValue + Config.cumulatedWallLoss + interactionLoss!! // 10-> constante (atenuação por parede)



        buildPropagationTree(// volta a construir a árvore, com as atualizações feitas

            neighborIndex,

            destiny,

            allSubregions,

            updatedVisited,

            current,

            parent,

            totalDist,

            apOrigin,

            targetPoint,

            updatedPropValue

        )

    }

     return if (children.isNotEmpty())

        Node(current, children, wallDist, totalDist, updatedPropValue)

    else

        Leaf(totalDist, updatedPropValue)

}

fun findLowestAttenuation(

    current: Int,

    destiny: Int,

    allSubregions: List<DENSITY>,

    visited: Set<Int> = emptySet(),

    parent: Int? = null,

    grandparent: Int? = null,

    parentTotalDist: Double = 0.0,

    apOrigin: COORDINATES,

    targetPoint: COORDINATES,

    propagationValue: Double = 0.0,

    bestPropagationValue: Double = INF_ATTENUATION

): Double {

    val updatedVisited = visited + current

    val currentRegion = allSubregions[current]
    val parentRegion = parent?.let { allSubregions[it] }
    var grandParentRegion = grandparent?.let { allSubregions[it] }


    if (current == destiny) { // se a região atual for a de destino , então vai colocar a distância até ao ponto exato de destino

        var interactionLoss : Double = 0.0
        if (parentRegion != null ) {
            var a: COORDINATES
            if (grandParentRegion == null) a = apOrigin
            else a = wallMidpoint(grandParentRegion, parentRegion)
            interactionLoss = getinteractionLoss(a ,parentRegion,currentRegion,targetPoint)
        }

        val toDest = calculateUntilDestinyExactPoint(apOrigin,parentRegion,currentRegion,targetPoint)

        val totalDist = parentTotalDist + toDest
        val distLoss = if (totalDist >0.0) Config.atenInDistRef + 10 * Config.n * log10(totalDist / Config.dRef) else 0.0
        return distLoss + propagationValue + interactionLoss
    }

    var wallDist = 0.0
    var totalDist = 0.0
    var updatedPropValue = 0.0
    var updatedBestPropagationValue = bestPropagationValue

    val nextIndices = currentRegion.adjacencyRegionsIndexes
        ?.filter { it !in updatedVisited }
        .orEmpty()

    val children = nextIndices.mapNotNull { neighborIndex ->

        val neighborRegion = allSubregions[neighborIndex]

        wallDist = getWallDist(parentRegion,currentRegion,neighborRegion,allSubregions,apOrigin)

        totalDist = parentTotalDist + wallDist // atualiza a distancia total

        var interactionLoss : Double? = 0.0
        if (parentRegion != null ) {
            var a: COORDINATES
            if (grandParentRegion == null) a = apOrigin
            else a = wallMidpoint(grandParentRegion, parentRegion)
            interactionLoss = getinteractionLoss(a ,parentRegion,currentRegion,neighborRegion)
        }

        updatedPropValue = propagationValue + Config.cumulatedWallLoss + interactionLoss!! // 10-> constante (atenuação por parede)

        // Poda: verifica se atenuação até aqui ainda é menor que a melhor encontrada até agora.
        val distLoss = if (totalDist >0.0) Config.atenInDistRef + 10 * Config.n * log10(totalDist / Config.dRef) else 0.0
        val newAttenuationValue = if (distLoss + updatedPropValue < updatedBestPropagationValue)

            findLowestAttenuation(// volta a construir a árvore, com as atualizações feitas

                neighborIndex,

                destiny,

                allSubregions,

                updatedVisited,

                current,

                parent,

                totalDist,

                apOrigin,

                targetPoint,

                updatedPropValue,

                updatedBestPropagationValue

            )
        else {
            INF_ATTENUATION
        }

        if (newAttenuationValue < updatedBestPropagationValue) updatedBestPropagationValue = newAttenuationValue

        newAttenuationValue
    }

    return if (children.isNotEmpty())

        children.min()

    else

        INF_ATTENUATION

}

fun findLowestAttenuationSorted(

    current: Int,

    destiny: Int,

    allSubregions: List<DENSITY>,

    visited: Set<Int> = emptySet(),

    parent: Int? = null,

    grandparent: Int? = null,

    parentTotalDist: Double = 0.0,

    apOrigin: COORDINATES,

    targetPoint: COORDINATES,

    propagationValue: Double = 0.0,

    bestPropagationValue: Double = INF_ATTENUATION

): Double {

    val updatedVisited = visited + current

    val currentRegion = allSubregions[current]
    val parentRegion = parent?.let { allSubregions[it] }
    var grandParentRegion = grandparent?.let { allSubregions[it] }


    if (current == destiny) { // se a região atual for a de destino , então vai colocar a distância até ao ponto exato de destino

        var interactionLoss : Double = 0.0
        if (parentRegion != null ) {
            var a: COORDINATES
            if (grandParentRegion == null) a = apOrigin
            else a = wallMidpoint(grandParentRegion, parentRegion)
            interactionLoss = getinteractionLoss(a ,parentRegion,currentRegion,targetPoint)
        }

        val toDest = calculateUntilDestinyExactPoint(apOrigin,parentRegion,currentRegion,targetPoint)

        val totalDist = parentTotalDist + toDest
        val distLoss = if (totalDist >0.0) Config.atenInDistRef + 10 * Config.n * log10(totalDist / Config.dRef) else 0.0
        return distLoss + propagationValue + interactionLoss
    }

    var wallDist = 0.0
    var totalDist = 0.0
    var updatedPropValue = 0.0
    var updatedBestPropagationValue = bestPropagationValue

    var nextIndices = currentRegion.adjacencyRegionsIndexes
        ?.filter { it !in updatedVisited }
        .orEmpty()

    // Ordenar nextIndices de acordo com a proximidade com target
    nextIndices = nextIndices.map { it ->
        Pair<Int, Double>(it, distanceCalculator(wallMidpoint(currentRegion, allSubregions[it]), targetPoint))
    }.sortedBy { it.second }.map { it ->
        it.first
    }

    val children = nextIndices.mapNotNull { neighborIndex ->

        val neighborRegion = allSubregions[neighborIndex]

        wallDist = getWallDist(parentRegion,currentRegion,neighborRegion,allSubregions,apOrigin)

        totalDist = parentTotalDist + wallDist // atualiza a distancia total

        var interactionLoss : Double? = 0.0
        if (parentRegion != null ) {
            var a: COORDINATES
            if (grandParentRegion == null) a = apOrigin
            else a = wallMidpoint(grandParentRegion, parentRegion)
            interactionLoss = getinteractionLoss(a ,parentRegion,currentRegion,neighborRegion)
        }

        updatedPropValue = propagationValue + Config.cumulatedWallLoss + interactionLoss!! // 10-> constante (atenuação por parede)

        // Poda: verifica se atenuação até aqui ainda é menor que a melhor encontrada até agora.
        val distLoss = if (totalDist >0.0) Config.atenInDistRef + 10 * Config.n * log10(totalDist / Config.dRef) else 0.0
        val newAttenuationValue = if (distLoss + updatedPropValue < updatedBestPropagationValue)

            findLowestAttenuationSorted(// volta a construir a árvore, com as atualizações feitas

                neighborIndex,

                destiny,

                allSubregions,

                updatedVisited,

                current,

                parent,

                totalDist,

                apOrigin,

                targetPoint,

                updatedPropValue,

                updatedBestPropagationValue

            )
        else {
            INF_ATTENUATION
        }

        if (newAttenuationValue < updatedBestPropagationValue) updatedBestPropagationValue = newAttenuationValue

        newAttenuationValue
    }

    return if (children.isNotEmpty())

        children.min()

    else

        INF_ATTENUATION

}