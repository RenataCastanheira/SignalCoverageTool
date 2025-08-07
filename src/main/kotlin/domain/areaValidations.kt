package domain

import model.AREA
import model.COORDINATES
import model.DENSITY


//Estas vals vao ser necessárias em varias funçoes deste modulo
/*
val buildingArea = areaReader()

val subRegionsAreas = densitiesReader()

 */


fun isValidSubRegion(buildingArea: AREA, subRegionsAreas: List<DENSITY>) : Boolean {

    for (subRegion in subRegionsAreas) {


        //verifica se a sub-região tem um tamanho inválido
        if (subRegion.subRegionArea.width == 0 && subRegion.subRegionArea.height == 0) return false


        //verifica se a sub-regiao está fora dos limites da area
        if (subRegion.x + subRegion.subRegionArea.width > buildingArea.width ||

            subRegion.y + subRegion.subRegionArea.height > buildingArea.height

        ) return false


    }

    return true

}


//verifica se existem areas sobrepostas na lista de sub-regioes
/*
 Antes de correr a função é necessáro:
 - verfificar se as regioes são validas (isValidSubregion)
 */
fun areTheAreasOverlapping(buildingArea: AREA, subRegionsAreas:List<DENSITY>) :Boolean{

    var subRegionsValidated = mutableListOf<DENSITY>()//esta variável contém as sub-regiões válidas

    var counter = 1 //serve para indicar a sub-regiao da lista de sub-regioes com que estamos a trabalhar

    for (subRegion in subRegionsAreas) {


        // verifica se há sobreposição com sub-regioes validadas
        for (subRegionValidated in subRegionsValidated) {

            if (isOverlapping(subRegion, subRegionValidated)) return true

        }

        //armazena a sub-regiao validada Às sub-regiões validadas
        subRegionsValidated += subRegion

        println("A subregion area $counter is valid")

        counter++

    }

    println("All the subregions areas are valid!\n")

    return false

}


//função auxiliar para verificar se há sobreposição entre 2 regiões
fun isOverlapping(A: DENSITY, B: DENSITY): Boolean {

    val aPoints = mutableListOf<COORDINATES>()

    aPoints.add(COORDINATES(A.x, A.y + A.subRegionArea.height))//aTopRight
    aPoints.add(COORDINATES(A.x + A.subRegionArea.width, A.y + A.subRegionArea.height))//aTopLeft
    aPoints.add(COORDINATES(A.x , A.y))//aBottomRight
    aPoints.add(COORDINATES(A.x + A.subRegionArea.width, A.y))//aBottomLeft
    aPoints.add(COORDINATES(A.x + A.subRegionArea.width/2 ,A.y + A.subRegionArea.height/2))//aCenter

    val bPoints = mutableListOf<COORDINATES>()
    bPoints.add(COORDINATES(B.x, B.y + B.subRegionArea.height))//aTopRight
    bPoints.add(COORDINATES(B.x + B.subRegionArea.width, B.y + B.subRegionArea.height))//aTopLeft
    bPoints.add(COORDINATES(B.x , B.y))//aBottomRight
    bPoints.add(COORDINATES(B.x + B.subRegionArea.width, B.y))//aBottomLeft
    bPoints.add(COORDINATES(B.x + B.subRegionArea.width/2 ,B.y + B.subRegionArea.height/2))//aCenter

    for(aPoint in aPoints){
        if (isCoordinateInsideRegion(aPoint, B)) return true
    }

    for(bPoint in bPoints){
        if (isCoordinateInsideRegion(bPoint, A)) return true
    }

    return false

}


fun isCoordinateInsideRegion(point: COORDINATES, subregion: DENSITY): Boolean {

    return if (subregion.x < point.x  &&  point.x <(subregion.x + subregion.subRegionArea.width)
        &&   subregion.y< point.y  && point.y < (subregion.y + subregion.subRegionArea.height) ) true
    else false

}


/**
 * verificações previas:
 * - ter a certeza de que nenuma subregiao se sobrepoe (areTheAreasOverlapping())
 */
fun doTheSubRegionsCoverAllArea(buildingArea: AREA, subRegions:List<DENSITY>) :Boolean{

    var area = 0

    for (subRegion in subRegions){

        area += subRegion.subRegionArea.width * subRegion.subRegionArea.height

    }

    val totalArea = buildingArea.height * buildingArea.width

    return if (area == totalArea)  true else false

}


/*
 * verifica se duas subregioes são(true) ou não(false) adjecentes
 */
fun checkAdjacencyBetweenTwoRegions(region1: DENSITY, region2: DENSITY): Boolean {

    return try {

        getContactWallEndpoints(region1, region2)

        true

    } catch (e: IllegalArgumentException) {

        false

    }

}

/*
 * esta função retorna a lista de indices das sub-regiões adjacentes a uma dada sub-região passada
   como parametro, atualizando também esse dado na data class
 */
fun getAdjacentRegionIndexes(region: DENSITY, allRegions: List<DENSITY>): List<Int> {

    var adjacencyRegionIndexes = mutableListOf<Int>()

    for (idx in 0 until allRegions.size ) {

        if (allRegions[idx] != region && checkAdjacencyBetweenTwoRegions(region, allRegions[idx])){
            adjacencyRegionIndexes.add(idx)
        }
    }

    region.adjacencyRegionsIndexes = adjacencyRegionIndexes

    return adjacencyRegionIndexes
}


/**
 * Retorna os extremos (ponto A e ponto B) da parede em contacto entre duas regiões adjacentes.
 * Assume que as regiões são sempre adjacentes. Se não forem, uma exceção será lançada.
 */
fun getContactWallEndpoints(wall1: DENSITY, wall2: DENSITY): Pair<COORDINATES, COORDINATES> {


    val x1 = wall1.x

    val y1 = wall1.y

    val w1 = wall1.subRegionArea.width

    val h1 = wall1.subRegionArea.height


    val x2 = wall2.x

    val y2 = wall2.y

    val w2 = wall2.subRegionArea.width

    val h2 = wall2.subRegionArea.height


    // Verifica adjacência vertical (paredes laterais)
    if (x1 + w1 == x2 || x2 + w2 == x1) {

        val commonX = if (x1 + w1 == x2) x1 + w1 else x2 + w2


        val startY = maxOf(y1, y2)

        val endY = minOf(y1 + h1, y2 + h2)


        if (startY < endY) {

            return Pair(COORDINATES(commonX, startY), COORDINATES(commonX, endY))

        }

    }


    // Verifica adjacência horizontal (paredes superior/inferior)
    if (y1 + h1 == y2 || y2 + h2 == y1) {

        val commonY = if (y1 + h1 == y2) y1 + h1 else y2 + h2


        val startX = maxOf(x1, x2)

        val endX = minOf(x1 + w1, x2 + w2)


        if (startX < endX) {

            return Pair(COORDINATES(startX, commonY), COORDINATES(endX, commonY))

        }

    }


    throw IllegalArgumentException("As sub-regiões não são adjacentes!")

}


/**
 * Calcula o ponto médio da parede em contacto entre duas sub-regiões adjacentes.
 * Chama a função auxiliar que retorna os extremos da parede e, em seguida, retorna o ponto médio.
 */
fun wallMidpoint(wall1: DENSITY, wall2: DENSITY): COORDINATES {

    val (pointA, pointB) = getContactWallEndpoints(wall1, wall2)


    // Calcula o ponto médio entre pointA e pointB
    val midX = (pointA.x + pointB.x) / 2

    val midY = (pointA.y + pointB.y) / 2


    return COORDINATES(midX, midY)

}




//retorna o indice da sub-região na lista de subregiões
fun whichRegionDoesThePointBelong (point:COORDINATES, subRegionsAreas: List<DENSITY>):DENSITY?{

    for (subregion in subRegionsAreas){

        if (point.x in subregion.x..(subregion.x + subregion.subRegionArea.width)
            && point.y in subregion.y..(subregion.y + subregion.subRegionArea.height)){

            //println("The point belongs to the subregion $subregion")

            return subregion

        }

    }

    //println("the point doesn't belong to any subregion")

    return null

}

fun validateData(buildingArea: AREA, subRegionsAreas: List<DENSITY>): Boolean {
    if (!isValidSubRegion(buildingArea, subRegionsAreas)) {
        println("The subregions are invalid!")
        return false
    }

    // TODO: verificar a função de overlapping

    if (areTheAreasOverlapping(buildingArea, subRegionsAreas)) {
        println("The subregions overlap!")
        return false
    }



    if (!doTheSubRegionsCoverAllArea(buildingArea, subRegionsAreas)) {
        println("The subregions don't cover the building area!")
        return false
    }



    println("The data is valid!\n")
    return true
}