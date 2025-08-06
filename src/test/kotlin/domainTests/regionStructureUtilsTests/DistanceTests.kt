package domainTests.regionStructureUtilsTests

import domain.*
import model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
/*
class DistanceTests {

    // Função auxiliar para printar a árvore com distâncias
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
        // Sub-regiões com valores fictícios
        val subRegion0 = DENSITY(0, 0, AREA(10, 10), listOf(1, 2), 1.0)
        val subRegion1 = DENSITY(10, 0, AREA(10, 10), listOf(0, 3), 2.0)
        val subRegion2 = DENSITY(0, 10, AREA(10, 10), listOf(0), 3.0)
        val subRegion3 = DENSITY(10, 10, AREA(10, 10), listOf(1), 4.0)

        val allSubregions = listOf(subRegion0, subRegion1, subRegion2, subRegion3)

        val tree = buildPropagationTree(0, 3, allSubregions)

        val treeStr = treeToString(tree, allSubregions)

        println(treeStr)

        // Como estamos a usar distâncias baseadas em pontos previsíveis, vamos validar a estrutura e valores
        assertTrue(treeStr.contains("Node(value=1.0")) // raiz
        assertTrue(treeStr.contains("Node(value=2.0")) // subRegion1
        assertTrue(treeStr.contains("Node(value=4.0")) // subRegion3
        assertTrue(treeStr.contains("dist="))          // verifica que distâncias são atribuídas
        assertTrue(treeStr.contains("total="))         // verifica que totalDist está presente
    }

    @Test
    fun `should return Leaf when current equals destiny`() {
        val subRegion = DENSITY(0, 0, AREA(10, 10), listOf(), 1.0)
        val allSubregions = listOf(subRegion)

        val tree = buildPropagationTree(
            current = 0,
            destiny = 0,
            allSubregions = allSubregions,
            parent = null,
            parentTotalDist = 0,
            visited = emptySet(),
            parentTotalDist = 0,
            parent = null,
            )

        assertEquals("Leaf", treeToString(tree, allSubregions))
    }

    @Test
    fun `should return Leaf if there are no unvisited neighbors`() {
        val subRegion0 = DENSITY(0, 0, AREA(10, 10), listOf(1), 1.0)
        val subRegion1 = DENSITY(10, 0, AREA(10, 10), listOf(), 2.0)
        val allSubregions = listOf(subRegion0, subRegion1)

        val tree = buildPropagationTree(0, 2, allSubregions)

        assertEquals("Leaf", treeToString(tree, allSubregions))
    }
}

 */