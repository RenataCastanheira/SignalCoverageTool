package domainTests.areaValidationTests

import domain.checkAdjacencyBetweenTwoRegions
import model.DENSITY
import model.AREA
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class checkAdjacencyBetweenTwoRegionsTest {

    // Inclui aqui a função getContactWallEndpoints e adjacencyRelationBetweenTwoRegions

    @Test
    fun `Regiões adjacentes verticalmente devem retornar true`() {
        val region1 = DENSITY(0, 0, AREA(10, 10),null,1.0)
        val region2 = DENSITY(10, 0, AREA(10, 10),null,1.0) // à direita de region1

        val result = checkAdjacencyBetweenTwoRegions(region1, region2)

        assertTrue(result)
    }

    @Test
    fun `Regiões adjacentes horizontalmente devem retornar true`() {
        val region1 = DENSITY(0, 0, AREA(10, 10), null,1.0)
        val region2 = DENSITY(0, 10,AREA (10, 10), null,1.0) // abaixo de region1

        val result = checkAdjacencyBetweenTwoRegions(region1, region2)

        assertTrue(result)
    }

    @Test
    fun `Regiões não adjacentes devem retornar false`() {
        val region1 = DENSITY(0, 0, AREA(10, 10),null,1.0)
        val region2 = DENSITY(20, 20, AREA(10, 10),null,1.0) // longe de region1

        val result = checkAdjacencyBetweenTwoRegions(region1, region2)

        assertFalse(result)
    }

    @Test
    fun `Regiões tocando apenas por um canto não são adjacentes`() {
        val region1 = DENSITY(0, 0, AREA(10, 10),null,1.0)
        val region2 = DENSITY(10, 10, AREA(10, 10),null,1.0) // canto inferior direito de region1

        val result = checkAdjacencyBetweenTwoRegions(region1, region2)

        assertFalse(result)
    }

    @Test
    fun `Regiões sobrepostas não são consideradas adjacentes`() {
        val region1 = DENSITY(0, 0, AREA(10, 10),null,1.0)
        val region2 = DENSITY(5, 5, AREA(10, 10),null,1.0) // sobreposição

        val result = checkAdjacencyBetweenTwoRegions(region1, region2)

        assertFalse(result)
    }
}
