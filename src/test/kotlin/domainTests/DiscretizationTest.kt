package domainTests

import model.AREA
import model.COORDINATES
import org.example.domain.*
import kotlin.test.*

class DiscretizationTest {

    @Test
    fun `testa pontos gerados corretamente para uma area 200x200`() {
        targetPoints.clear()
        val area = AREA(width = 200, height = 200)
        discretization(COORDINATES(0, 0), area)

        // Espera-se uma grelha de 3x3 pontos: (0,0), (0,100), (0,200), (100,0), ...
        assertEquals(9, targetPoints.size)
        assertTrue(COORDINATES(0, 0) in targetPoints)
        assertTrue(COORDINATES(100, 100) in targetPoints)
        assertTrue(COORDINATES(200, 200) in targetPoints)
    }
}