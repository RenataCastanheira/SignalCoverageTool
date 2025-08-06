package domainTests.regionStructureUtilsTests

import kotlin.test.*
import domain.*
import model.AREA
import model.COORDINATES
import model.DENSITY
import org.example.domain.propagation.*

class BuildPropagationTreeTest {


    // Cria 3 regiões alinhadas em linha reta: 0 → 1 → 2
    private fun createLinearRegions(): List<DENSITY> {
        val area = AREA(width = 10, height = 10) // área dummy só para testes

        return listOf(
            DENSITY(
                x = 0,
                y = 0,
                subRegionArea = area,
                adjacencyRegionsIndexes = listOf(1),
                value = 1.0
            ),
            DENSITY(
                x = 10,
                y = 0,
                subRegionArea = area,
                adjacencyRegionsIndexes = listOf(0, 2),
                value = 1.0
            ),
            DENSITY(
                x = 20,
                y = 0,
                subRegionArea = area,
                adjacencyRegionsIndexes = listOf(1),
                value = 1.0
            )
        )
    }

    @Test
    fun `test single step from origin to destiny`() {
        val regions = createLinearRegions()

        val tree = buildPropagationTree(
            current = 0,
            destiny = 1,
            allSubregions = regions,
            apOrigin = COORDINATES(0,0),
            targetPoint = COORDINATES(10,0)
        )

        val node = tree as Node

        // deve ter apenas 1 filho (o destino)
        assertEquals(0, node.region)
        assertEquals(1, node.children.size)
        val child = node.children.first() as Node
        assertEquals(1, child.region)

        // distâncias devem ser positivas
        assertTrue(child.dist > 0)
        assertTrue(child.totalDist > 0)

        // atenuação deve ser pelo menos 10
        assertTrue(child.atenValue >= 10.0)
    }

    @Test
    fun `test two hops propagation`() {
        val regions = createLinearRegions()

        val tree = buildPropagationTree(
            current = 0,
            destiny = 2,
            allSubregions = regions,
            apOrigin = COORDINATES(0,0),
            targetPoint = COORDINATES(20,0)
        )

        val node0 = tree as Node
        assertEquals(0, node0.region)

        val node1 = node0.children.first() as Node
        assertEquals(1, node1.region)

        val node2 = node1.children.first() as Leaf
        assertEquals(node2 is Leaf, true)

        // Cada hop soma pelo menos 10 dB
        assertTrue(node2.atenValue >= 20.0)

        print(node2.atenValue)

        // TotalDist deve ser > dist
        assertTrue(node2.totalDist > node1.totalDist)
    }

    @Test
    fun `test two hops attenuation`() {
        val regions = createLinearRegions()

        val attenuationValue = findLowestAttenuation(
            current = 0,
            destiny = 2,
            allSubregions = regions,
            apOrigin = COORDINATES(0,0),
            targetPoint = COORDINATES(20,0)
        )

        // Cada hop soma pelo menos 10 dB
        assertTrue(attenuationValue >= 20.0)

        print(attenuationValue)
    }

    @Test
    fun `test Leaf when no children`() {
        val isolatedRegion = listOf(
            DENSITY(
                x = 0,
                y = 0,
                subRegionArea = AREA(0,0),
                adjacencyRegionsIndexes = null,
                value = 0.0
            )
        )

        val tree = buildPropagationTree(
            current = 0,
            destiny = 0,
            allSubregions = isolatedRegion,
            apOrigin = COORDINATES(0,0),
            targetPoint = COORDINATES(0,0)
        )

        assertTrue(tree is Leaf)
        val leaf = tree as Leaf
        assertEquals(0.0, leaf.totalDist)
        assertEquals(0.0, leaf.propValue)
    }
}