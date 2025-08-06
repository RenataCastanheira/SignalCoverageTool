package domainTests.regionStructureUtilsTests

import domain.*
import model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
/*
class DistanceTests {

    // Função auxiliar para printar a árvore com distâncias (para debugging se necessário)
    private fun treeToString(tree: PropagationTree, allSubregions: List<DENSITY>): String {
        return when (tree) {
            is Node -> {
                val value = allSubregions[tree.region].value
                val childrenStr = tree.children.joinToString(", ") { treeToString(it, allSubregions) }
                "Node(value=$value, dist=${tree.dist}, total=${tree.totalDist}) -> [$childrenStr]"
            }
            is Leaf -> "Leaf"
        }
    }

    @Test
    fun `should build a propagation tree with correct distances`() {
        // Sub-regiões com coordenadas e ligações bem definidas
        val subRegion0 = DENSITY(0, 0, AREA(10, 10), listOf(1, 2), 1.0)     // apOrigin
        val subRegion1 = DENSITY(10, 0, AREA(10, 10), listOf(0, 3), 2.0)
        val subRegion2 = DENSITY(0, 10, AREA(10, 10), listOf(0), 3.0)
        val subRegion3 = DENSITY(10, 10, AREA(10, 10), listOf(1), 4.0)     // apDestiny

        val all = listOf(subRegion0, subRegion1, subRegion2, subRegion3)

        val tree = buildPropagationTree(
            current = 0,
            destiny = 3,
            allSubregions = all,
            parent = null,
            parentTotalDist = 0,
            apOrigin = COORDINATES(5, 6),
            apDestiny = COORDINATES(15, 16)
        )

        // Verificações estruturais
        assertTrue(tree is Node)
        val node0 = tree as Node
        assertEquals(0, node0.region)
        assertEquals(0.0, node0.dist)
        assertEquals(0.0, node0.totalDist)

        // Um dos filhos deve ser a região 1 (ligada à 0), e depois a 3 (destino)
        val node1 = node0.children.find { it is Node && it.region == 1 } as? Node
        assertNotNull(node1)

        val node3 = node1!!.children.find { it is Node && it.region == 3 } as? Node
        assertNotNull(node3)


        // Verificar distâncias reais entre pontos médios:
        val expectedDist0to1 =  wallMidpoint(subRegion0, subRegion1)
        val expectedDist1to3 =  wallMidpoint(subRegion1, subRegion3)


        assertEquals(expectedDist0to1, node1.dist)
        assertEquals(expectedDist0to1, node1.totalDist)

        assertEquals(expectedDist1to3, node3!!.dist)

        val totalDist = expectedDist0to1.plus(expectedDist1to3)
        assertEquals(expectedDist0to1 + expectedDist1to3, node3.totalDist)
    }

    @Test
    fun `should return Leaf when current equals destiny`() {
        val region = DENSITY(0, 0, AREA(10, 10), listOf(), 1.0)

        val tree = buildPropagationTree(
            current = 0,
            destiny = 0,
            allSubregions = listOf(region),
            parent = null,
            parentTotalDist = 0,
            apOrigin = COORDINATES(5, 6),
            apDestiny = COORDINATES(5, 6)
        )

        assertTrue(tree is Leaf)
    }

    @Test
    fun `should return Leaf if there are no unvisited neighbors`() {
        val subRegion0 = DENSITY(0, 0, AREA(10, 10), listOf(1), 1.0)
        val subRegion1 = DENSITY(10, 10, AREA(10, 10), listOf(), 2.0)

        val all = listOf(subRegion0, subRegion1)

        val tree = buildPropagationTree(
            current = 0,
            destiny = 2, // região 2 não existe, mas testamos se 0 -> 1 é visitado, e 1 não tem vizinhos
            allSubregions = all,
            parent = null,
            parentTotalDist = 0,
            apOrigin = COORDINATES(5, 6),
            apDestiny = COORDINATES(15,16)
        )

        assertTrue(tree is Leaf)
    }
}

 */