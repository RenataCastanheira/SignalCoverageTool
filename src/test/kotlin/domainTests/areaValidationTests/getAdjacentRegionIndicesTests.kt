package domainTests.areaValidationTests

import domain.getAdjacentRegionIndexes
import model.AREA
import model.DENSITY
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class getAdjacencyRegionIndicesTests {


    @Test
    fun testGetAdjacentRegionIndices() {
        // Cria 4 regiões numa grelha 2x2
        val region0 = DENSITY(0, 0, AREA(10,10),null, value = 1.0)
        val region1 = DENSITY(10, 0, AREA(10,10),null, value = 2.0)
        val region2 = DENSITY(0, 10, AREA(10,10),null, value = 3.0)
        val region3 = DENSITY(10, 10, AREA(10,10),null, value = 4.0)

        val allRegions = listOf(region0, region1, region2, region3)

        // Estamos a testar a região (0,0), que deve ser adjacente a (10,0) e (0,10)
        val result = getAdjacentRegionIndexes(region0, allRegions)

        // Espera-se que os índices 1 e 2 sejam devolvidos
        assertEquals(listOf(1, 2), result)

        // Também deve ter atualizado a propriedade adjacencyRegionsIndexes
        assertEquals(listOf(1, 2), region0.adjacencyRegionsIndexes)
    }
}
