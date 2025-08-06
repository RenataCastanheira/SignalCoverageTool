package domainTests.regionStructureUtilsTests

import org.example.domain.Config
import org.example.domain.Config.dRef
import org.example.domain.propagation.*
import kotlin.test.*
import kotlin.math.log10

class PropagationUtilsTest {

    @BeforeTest
    fun setup() {
        // limpar variáveis globais antes de cada teste
        results.clear()
        minValue = null
    }

    @Test
    fun `test atenuationValues with single Leaf`() {
        val leaf = Leaf(
            totalDist = 200.0,  // 2 metros
            atenValue = 15.0,   // já acumulado
            propValue = 15.0
        )

        val values = atenuationValues(leaf)

        // cálculo esperado: PL0 + 10 * n * log10(d/dRef)
        val expectedDistLoss = Config.atenInDistRef + 10 * Config.n * log10(200.0 / dRef) // 40 + 20*log10(2)
        val expectedTotal = 15.0 + expectedDistLoss

        assertEquals(1, values.size)
        assertEquals(expectedTotal, values.first(), 0.0001)
        assertEquals(expectedTotal, leaf.propValue, 0.0001)
    }

    @Test
    fun `test atenuationValues with tree of Nodes and Leaves`() {
        val leaf1 = Leaf(totalDist = 100.0, atenValue = 10.0, propValue = 10.0)
        val leaf2 = Leaf(totalDist = 400.0, atenValue = 20.0, propValue = 20.0)

        val node = Node(
            region = 0,
            children = listOf(leaf1, leaf2),
            dist = 0.0,
            totalDist = 0.0,
            atenValue = 0.0
        )

        val values = atenuationValues(node)

        assertEquals(2, values.size)

        // Para leaf1:
        val expected1 = 10.0 + (Config.atenInDistRef + 10 * Config.n * log10(100.0 / dRef))
        // Para leaf2:
        val expected2 = 20.0 + (Config.atenInDistRef + 10 * Config.n * log10(400.0 / dRef))

        assertTrue(values.contains(expected1))
        assertTrue(values.contains(expected2))
        assertEquals(expected1, leaf1.propValue, 0.0001)
        assertEquals(expected2, leaf2.propValue, 0.0001)
    }

    @Test
    fun `test findMinAtenuationValue with multiple leaves`() {
        val leaf1 = Leaf(totalDist = 100.0, atenValue = 30.0, propValue = 30.0)
        val leaf2 = Leaf(totalDist = 150.0, atenValue = 25.0, propValue = 25.0)
        val leaf3 = Leaf(totalDist = 50.0, atenValue = 35.0, propValue = 35.0)

        val node = Node(
            region = 0,
            children = listOf(leaf1, leaf2, leaf3),
            dist = 0.0,
            totalDist = 0.0,
            atenValue = 0.0
        )

        // Primeiro aplicamos atenuationValues para atualizar os valores
        atenuationValues(node)

        val min = findMinAtenuationValue(node)

        // Encontrar o menor após atenuação
        val computedValues = listOf(leaf1.propValue, leaf2.propValue, leaf3.propValue)
        val expectedMin = computedValues.minOrNull()

        assertEquals(expectedMin, min)
    }

    @Test
    fun `test findMinAtenuationValue with single leaf`() {
        val leaf = Leaf(totalDist = 250.0, atenValue = 50.0, propValue = 50.0)

        atenuationValues(leaf) // atualiza o valor

        val min = findMinAtenuationValue(leaf)

        assertEquals(leaf.propValue, min)
    }
}