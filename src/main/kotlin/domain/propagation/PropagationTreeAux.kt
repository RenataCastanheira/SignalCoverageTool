package domain.propagation

import org.example.domain.propagation.aAngleFormule
import domain.wallMidpoint
import model.COORDINATES
import model.DENSITY
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.sqrt


// esta função calcula o angulo entre dois vetores
fun directionChangeAngle(a: COORDINATES, b: COORDINATES, c: COORDINATES): Double {

    // Vetor: AB = (p2 - p1)
    val abX = (b.x - a.x).toDouble()
    val abY = (b.y - a.y).toDouble()

    //BC = (p3 - p2)
    val bcX = (c.x - b.x).toDouble()
    val bcY = (c.y - b.y).toDouble()

    val prodescalar = abX * bcX + abY * bcY
    val cmpAB = distanceCalculator(a, b)
    val cmpBC = distanceCalculator(b, c)

    return if (cmpAB == 0.0 || cmpBC == 0.0) {
        0.0 // sem direção definida
    } else {
        var angulo = acos((prodescalar / (cmpAB * cmpBC))) * 180 / PI// esta multiplicação é para converter para graus

        //"angulo" está em Double

        if (angulo > 180.0) {
            angulo = 360.0 - angulo
        }

        return angulo
    }
}


// função que calcula a distancia entre dois pontos
fun distanceCalculator(mid1: COORDINATES, mid2: COORDINATES): Double {


    val dx = (mid1.x - mid2.x).toDouble()

    val dy = (mid1.y - mid2.y).toDouble()


    return sqrt(dx * dx + dy * dy)

}


fun getWallDist(parentRegion: DENSITY?, currentRegion: DENSITY, neighborRegion: DENSITY, allSubregions: List<DENSITY>, apOrigin: COORDINATES): Double{
    if (parentRegion != null) { // caso não seja a primeira dist a ser calculada
        val mid1 = wallMidpoint(parentRegion, currentRegion) //vai calcular o ponto médio da 1ª região

        val mid2 = wallMidpoint(currentRegion, neighborRegion) // calcular o ponto médio da próxima

        return distanceCalculator(mid1, mid2)// calcula a distância entre a parede anterior e a próxima

    } else {

        val mid = currentRegion.adjacencyRegionsIndexes

            ?.firstOrNull()

            ?.let { wallMidpoint(currentRegion, allSubregions[it]) }


        return mid?.let { distanceCalculator(apOrigin, it) } ?: 0.0

    }
}

fun getinteractionLoss(
    a: COORDINATES,
    parentRegion: DENSITY,
    currentRegion: DENSITY,
    neighborRegion: DENSITY
): Double?{
    val b = wallMidpoint(parentRegion, currentRegion)
    val c = wallMidpoint(currentRegion, neighborRegion)


    if (a != null && b != null && c != null) {
        val bj = directionChangeAngle(a,b,c)
        return bj * aAngleFormule //LBj= A.Bj
    }
    return null
}

// Versão alternativa da função em que o último ponto é definido por uma
// coordenada, e não por duas regiões.
fun getinteractionLoss(
    a: COORDINATES,
    parentRegion: DENSITY,
    currentRegion: DENSITY,
    c: COORDINATES
): Double{
    val b = wallMidpoint(parentRegion, currentRegion)

    val bj = directionChangeAngle(a,b,c)
    return bj * aAngleFormule //LBj= A.Bj
}

fun calculateUntilDestinyExactPoint(apOrigin: COORDINATES, parentRegion: DENSITY?, currentRegion: DENSITY, targetPoint: COORDINATES): Double{
    var lastWallMid = apOrigin
    if (parentRegion != null){
        lastWallMid = parentRegion.let { wallMidpoint(currentRegion, it) }

    }
    val toDest = lastWallMid?.let { distanceCalculator(it, targetPoint) } ?: 0.0

    return toDest

}


