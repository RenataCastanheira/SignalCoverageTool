package org.example.domain.propagation

import domain.propagation.calculateUntilDestinyExactPoint
import domain.propagation.directionChangeAngle
import domain.propagation.distanceCalculator
import domain.propagation.getWallDist
import domain.propagation.getinteractionLoss
import domain.wallMidpoint
import domain.whichRegionDoesThePointBelong

import model.COORDINATES
import model.DENSITY
import org.example.domain.Config
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min

/*
const val initialValue = 0

//valores constantes da seguinte formula: PL = Plo + 10 n log (d/do) + som(Lwi) + som(Lbj)
const val cumulatedWallLoss= 10 //dB
const val aAngleFormule = 0.0556 //dB/o
const val atenInDistRef = 40 //dB
const val n = 2
const val INF_ATTENUATION = 9999999999999.0 // dB

 */

data class Attenuation(var totalDist: Double = 0.0,
                       var otherLosses: Double = 0.0,
                       var totalAttenuation: Double = 0.0,
                       var traversedCoordinates: MutableList<COORDINATES>? = null
) : Comparable<Attenuation> {
    override fun compareTo(other: Attenuation): Int {

        if (this.totalAttenuation < other.totalAttenuation) return -1
        else if (this.totalAttenuation > other.totalAttenuation) return 1
        else return 0
    }
}

fun computeTotalAttenuation(dist: Double, otherLosses: Double): Double {

    val distLoss = if (dist > 0.0) Config.atenInDistRef + 10 * Config.n * log10(dist / Config.dRef) else 0.0
    return distLoss + otherLosses
}

fun findLowestAttenuationv2(

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

    bestPropagationValue: Double = INF_ATTENUATION,

    traversedCoordinates: MutableList<COORDINATES> = mutableListOf(),

    ): Attenuation {

    // Está é a primeira chamada? Se sim, iniciar o traversedCoordinates com as
    // coordenadas do AP.
    if (traversedCoordinates.size == 0) traversedCoordinates.add(apOrigin)

    val updatedVisited = visited + current

    val currentRegion = allSubregions[current]
    val parentRegion = parent?.let { allSubregions[it] }
    var grandParentRegion = grandparent?.let { allSubregions[it] }


    if (current == destiny) { // se a região atual for a de destino , então vai colocar a distância até ao ponto exato de destino

        // Adicionar ponto de destino à lista de pontos visitados
        traversedCoordinates.add(targetPoint)

        // Calcular o último interaction loss
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

        return Attenuation(
            parentTotalDist + toDest,
            propagationValue + interactionLoss,
            computeTotalAttenuation(parentTotalDist + toDest, propagationValue + interactionLoss),
            traversedCoordinates
        )
    }

    var wallDist = 0.0
    var totalDist = 0.0
    var updatedPropValue = 0.0
    var updatedBestPropagationValue = bestPropagationValue

    var nextIndices = currentRegion.adjacencyRegionsIndexes
        ?.filter { it !in updatedVisited }
        .orEmpty()

    // Ordenar nextIndices de acordo com a proximidade com target
    /*nextIndices = nextIndices.map { it ->
        Pair<Int, Double>(it, distanceCalculator(wallMidpoint(currentRegion, allSubregions[it]), targetPoint))
    }.sortedBy { it.second }.map { it ->
        it.first
    }*/

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
        if (computeTotalAttenuation(totalDist, updatedPropValue) < updatedBestPropagationValue) {

            // Adicionar o midpoint da parede como próximo ponto visitado.
            traversedCoordinates.add(wallMidpoint(currentRegion, neighborRegion))

            val newAttenuation = findLowestAttenuationv2(// volta a construir a árvore, com as atualizações feitas

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

                updatedBestPropagationValue,

                traversedCoordinates

            )
            if (newAttenuation.totalAttenuation < updatedBestPropagationValue) updatedBestPropagationValue = newAttenuation.totalAttenuation

            newAttenuation
        }
        else {
            null
        }
    }

    return if (children.isNotEmpty())

        children.min()

    else

        Attenuation(totalDist, INF_ATTENUATION, INF_ATTENUATION)

}

// Calcula os menores caminhos de cada subregion para cada outra subregion. "Caminho"
// aqui denota uma sequência subregiões adjacentes. Trata as subregiões como um
// grafo e usa o algoritmo de Floyd-Warshall para calcular os caminhos mais curtos.
fun FloydWarshall(allSubregions: List<DENSITY>): Array<Array<Int>> {

    val INF = allSubregions.size + 1
    val dist = Array(allSubregions.size) { Array<Int>(allSubregions.size) { INF } }
    val prev = Array(allSubregions.size) { Array<Int>(allSubregions.size) { -1 } }

    // Inicialização
    allSubregions.forEachIndexed { i, sr ->
        sr.adjacencyRegionsIndexes?.forEach { j ->

            dist[i][j] = 1
            prev[i][j] = i
        }

        dist[i][i] = 0
        prev[i][i] = i
    }

    // Loop principal
    allSubregions.forEachIndexed { i, sr1 ->
        allSubregions.forEachIndexed { j, sr2 ->
            allSubregions.forEachIndexed { k, sr3 ->

                if (dist[i][j] > dist[i][k] + dist[k][j]) {

                    dist[i][j] = dist[i][k] + dist[k][j]
                    prev[i][j] = prev[k][j]
                }
            }
        }
    }

    return prev
}

// Com base no menor caminho entre duas subregiões (parâmetro prev), constrói
// um possível caminho de propagação (não necessariamente o melhor). Caminho
// construído passa pela sequência de subregiões do caminho mais curto.
fun findAttenuationFromShortestPath(allSubregions: List<DENSITY>,
                                    prev: Array<Array<Int>>,
                                    src: COORDINATES,
                                    dst: COORDINATES): Attenuation {

    val attenuation = Attenuation()

    val srcSubRegion = allSubregions.indexOf(whichRegionDoesThePointBelong(src,allSubregions))
    val dstSubRegion = allSubregions.indexOf(whichRegionDoesThePointBelong(dst,allSubregions))

    // Calcular o caminho de trás para frente, desde a subregião que contém o ponto de destino.
    var currentRegion = dstSubRegion
    var parentRegion: Int? = null
    var grandParentRegion: Int? = null
    while(currentRegion != srcSubRegion) {

        val neighborRegion = prev[srcSubRegion][currentRegion]

        val wallDist = getWallDist(
            parentRegion?.let { allSubregions[it] },
            allSubregions[currentRegion],
            allSubregions[neighborRegion],
            allSubregions,
            dst
        )

        attenuation.totalDist = attenuation.totalDist + wallDist // atualiza a distancia total

        var interactionLoss : Double? = 0.0
        if (parentRegion != null ) {
            var a: COORDINATES
            if (grandParentRegion == null) a = dst
            else a = wallMidpoint(allSubregions[grandParentRegion], allSubregions[parentRegion])
            interactionLoss = getinteractionLoss(a, allSubregions[parentRegion], allSubregions[currentRegion], allSubregions[neighborRegion])
        }

        attenuation.otherLosses += Config.cumulatedWallLoss + interactionLoss!!

        grandParentRegion = parentRegion
        parentRegion = currentRegion
        currentRegion = neighborRegion
    }

    // Adicionar as componentes de atenuação devidas à última parte do percurso
    val toDest = calculateUntilDestinyExactPoint(dst, parentRegion?.let { allSubregions[it] }, allSubregions[currentRegion], src)
    attenuation.totalDist += toDest

    // O propósito desta função é encontrar apenas um limite superior para
    // a atenuação. Como um midpoint está entre duas regiões, é possível que
    // não estejamos a levar em conta as paredes iniciais. Então, vamos somar
    // o equivalente a mais duas paredes aqui, para ter certeza de que não
    // retornarmos uma atenuação abaixo do valor real. Se as paredes já tiverem
    // sido somadas, isto será corrigido quando a função findLowestAttenuationv2
    // for chamada.
    attenuation.otherLosses += 2 * Config.cumulatedWallLoss

    attenuation.totalAttenuation = computeTotalAttenuation(attenuation.totalDist, attenuation.otherLosses)

    return attenuation
}

// Acha a menor atenuação entre duas coordenadas, utilizando a informação pré-computada
// dos menores caminhos entre as subregiões para acelerar o processamento. Primeiro, calcula
// um caminho de propagação aproximado usando a função findAttenuationFromShortestPath. Depois,
// determina a menor atenuação com a função findLowestAttenuationv2, passando a atenuação da
// solução aproximada como critério de poda.
fun findLowestAttenuationFromShortestPath(allSubregions: List<DENSITY>,
                                    prev: Array<Array<Int>>,
                                    src: COORDINATES,
                                    dst: COORDINATES): Attenuation {

    val initialAttenuation = findAttenuationFromShortestPath(allSubregions, prev, src, dst)

    val srcRegionIndex = allSubregions.indexOf(whichRegionDoesThePointBelong(src,allSubregions))
    val dstRegionIndex = allSubregions.indexOf(whichRegionDoesThePointBelong(dst,allSubregions))

    val attenuation = findLowestAttenuationv2(
        current = srcRegionIndex,
        destiny = dstRegionIndex,
        allSubregions = allSubregions,
        apOrigin = src,
        targetPoint = dst,
        bestPropagationValue = initialAttenuation.totalAttenuation
    )

    return if (attenuation.totalAttenuation != INF_ATTENUATION) attenuation else initialAttenuation
}

// Representa um par de midpoints
data class WallMidpointPair(val srcR1: Int, val srcR2: Int, val dstR1: Int, val dstR2: Int)

// Função que cria um WallMidpointPair a partir das regiões. Útil apenas para definir um critério
// de representação, de forma que, por exemplo, um midpoint entre srcR1 e srcR2 tenha a mesma
// representação de um midpoint entre srcR2 e srcR1.
fun buildMidpointPair(srcR1: Int, srcR2: Int, dstR1: Int, dstR2: Int): WallMidpointPair {

    val srcFirst = min(srcR1, srcR2)
    val srcSecond = max(srcR1, srcR2)
    val dstFirst = min(dstR1, dstR2)
    val dstSecond = max(dstR1, dstR2)

    return WallMidpointPair(srcFirst, srcSecond, dstFirst, dstSecond)
}

// Determina as atenuações entre todas os possíveis pares de midpoints, usando a
// função findLowestAttenuationFromShortestPath.
fun findAllWallMidpointAttenuation(allSubregions: List<DENSITY>): HashMap<WallMidpointPair, Attenuation> {

    val prev = FloydWarshall(allSubregions)
    val output = HashMap<WallMidpointPair, Attenuation>()

    // Iterar por todas as regiões de origem e destino
    allSubregions.forEachIndexed { srcR1Index, srcR1 ->
        allSubregions.forEachIndexed { dstR1Index, dstR1 ->

            // Iterar por todas as regiões vizinhas de srcR1
            srcR1.adjacencyRegionsIndexes?.forEach { srcR2Index ->

                // Iterar por todas as regiões vizinhas de dstR1
                dstR1.adjacencyRegionsIndexes?.forEach { dstR2Index ->

                    // Verificar se este par de midpoints já existe no hashmap.
                    val midPointPair = buildMidpointPair(srcR1Index, srcR2Index, dstR1Index, dstR2Index)
                    if (!output.containsKey(midPointPair)) {

                        // Não. Calcular atenuação para este par de midpoints.
                        val src = wallMidpoint(srcR1, allSubregions[srcR2Index])
                        val dst = wallMidpoint(dstR1, allSubregions[dstR2Index])
                        val attenuation = findLowestAttenuationFromShortestPath(allSubregions, prev, src, dst)

                        // Adicionar atenuação ao hash map
                        output.put(midPointPair, attenuation)
                    }

                }
            }
        }
    }

    return output
}

// Calcula a atenuação entre duas coordenadas quaisquer, usando os midpoints como pontos de referência.
// Basicamente, identificam-se as subregiões de origem e destino, seus midpoints e testam-se todas as
// combinações de pares e verica-se qual resulta no melhor caminho de propagação completo.
fun findLowestAttenuationFromMidpoints(allSubregions: List<DENSITY>, midpointAttenuation: HashMap<WallMidpointPair, Attenuation>, src: COORDINATES, dst: COORDINATES) : Attenuation {

    // Descobrir regiões às quais pertencem src e dst
    val srcR1 = whichRegionDoesThePointBelong(src,allSubregions)
    val dstR1 = whichRegionDoesThePointBelong(dst,allSubregions)
    val srcR1Index = allSubregions.indexOf(srcR1)
    val dstR1Index = allSubregions.indexOf(dstR1)

    var lowestAttenuation = Attenuation(totalAttenuation = INF_ATTENUATION)

    // Caso especial: src e dst estão na mesma região:
    if (srcR1Index == dstR1Index) {

        // Atenuação é só pela distância
        lowestAttenuation.totalAttenuation = computeTotalAttenuation(distanceCalculator(src, dst), 0.0)
        return lowestAttenuation
    }

    // Iterar pelas regiões vizinhas a srcR1 e dstR1 para determinar as possíveis combinações de midpoints
    srcR1?.adjacencyRegionsIndexes?.forEach { srcR2Index ->

        dstR1?.adjacencyRegionsIndexes?.forEach { dstR2Index ->

            val midPointPair = buildMidpointPair(srcR1Index, srcR2Index, dstR1Index, dstR2Index)
            val attenuationToMidpoint = midpointAttenuation.get(midPointPair)

            if (attenuationToMidpoint != null) {

                val srcMidpoint = wallMidpoint(srcR1, allSubregions[srcR2Index])
                val dstMidpoint = wallMidpoint(dstR1, allSubregions[dstR2Index])
                val distanceToSrcMidpoint = distanceCalculator(src, srcMidpoint)
                val distanceToDstMidpoint = distanceCalculator(dst, dstMidpoint)

                val newAttenuation = Attenuation(attenuationToMidpoint.totalDist + distanceToSrcMidpoint + distanceToDstMidpoint, attenuationToMidpoint.otherLosses)
                // É preciso verificar se o attenuationToMidpoint já contabilizou as atenuações da primeira a da última paredes.
                // Para a primeira parede, basta verificar se o ponto src e o srcMidpoint estão na mesma subregião: se sim, a parede
                // já foi contabilizada; se não, precisamos somá-la aqui. Um raciocínio análogo pode ser usado para o destino.
                if (whichRegionDoesThePointBelong(dst, allSubregions) != whichRegionDoesThePointBelong(dstMidpoint, allSubregions))
                    newAttenuation.otherLosses += Config.cumulatedWallLoss
                if (whichRegionDoesThePointBelong(src, allSubregions) != whichRegionDoesThePointBelong(srcMidpoint, allSubregions))
                    newAttenuation.otherLosses += Config.cumulatedWallLoss

                // Também é preciso adicionar as atenuações por interação do início e do final do caminho.
                // Para isto, vamos criar a lista completa de coordenadas atravassadas, incluindo src e dst,
                // ao traversedCoordinates do objeto de newAttenuation.
                newAttenuation.traversedCoordinates = mutableListOf()
                newAttenuation.traversedCoordinates?.add(src)
                newAttenuation.traversedCoordinates?.addAll(attenuationToMidpoint.traversedCoordinates!!)
                newAttenuation.traversedCoordinates?.add(dst)
                // Por definição, há ao menos 3 coordenadas neste caminho. Então, sempre podemos calcular a perda por
                // interação das três primeiras coordenadas.
                newAttenuation.otherLosses += directionChangeAngle(
                    newAttenuation.traversedCoordinates!![0],
                    newAttenuation.traversedCoordinates!![1],
                    newAttenuation.traversedCoordinates!![2]
                ) * aAngleFormule
                // Para as três últimas coordenadas, só podemos fazer o cálculo se há mais que três coordenadas
                // (do contrário, a perda por interação das três últimas coordenadas é a mesma das três primeiras).
                newAttenuation.traversedCoordinates?.size?.let {
                    // Só há perda por interação caso haja 3 coordenadas em sequência. Logo, isto só ocorre
                    // se o attenuationToMidpoint passa por ao menos 2 coordenadas
                    if (it > 3) {

                        newAttenuation.otherLosses += directionChangeAngle(
                            newAttenuation.traversedCoordinates!![it-3],
                            newAttenuation.traversedCoordinates!![it-2],
                            newAttenuation.traversedCoordinates!![it-1]
                        ) * aAngleFormule
                    }
                }

                newAttenuation.totalAttenuation = computeTotalAttenuation(newAttenuation.totalDist, newAttenuation.otherLosses)

                if (newAttenuation < lowestAttenuation) lowestAttenuation = newAttenuation
            }
        }
    }

    return lowestAttenuation
}
