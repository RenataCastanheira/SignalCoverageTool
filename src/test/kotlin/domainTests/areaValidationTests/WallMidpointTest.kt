package domainTests.areaValidationTests

import domain.wallMidpoint
import model.AREA
import model.COORDINATES
import model.DENSITY
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WallMidpointTest {

    @Test
    fun `vertical adjacent walls should return correct midpoint`() {
        // wall1: define um retângulo na posição (0,0) com largura 10 e altura 20
        val wall1 = DENSITY(0, 0, AREA(10, 20), null,1.0)
        // wall2: adjacente verticalmente à direita de wall1, iniciando em x = 10, com y = 5, largura 10 e altura 10.
        val wall2 = DENSITY(10, 5, AREA(10, 10), null,1.0)

        // Para parede vertical:
        // commonX = 10.0; a interseção vertical ocorre entre y = max(0, 5)=5 e min(20, 15)=15
        // O ponto médio será: (10.0, (5+15)/2=10.0)
        val expected = COORDINATES(10, 10)
        val result = wallMidpoint(wall1, wall2)
        assertEquals(expected, result)
    }

    @Test
    fun `horizontal adjacent walls should return correct midpoint`() {
        // wall1: define um retângulo na posição (0,0) com largura 20 e altura 10
        val wall1 = DENSITY(0, 0, AREA(20, 10), null,1.0)
        // wall2: adjacente horizontalmente abaixo de wall1 (wall1 bottom = 10 equals wall2 top),
        // posicionado em x = 5 com largura 10, e altura 10
        val wall2 = DENSITY(5, 10, AREA(10, 10), null,1.0)

        // Para parede horizontal:
        // commonY = 10.0; a interseção horizontal ocorre entre x = max(0, 5)=5 e min(20, 15)=15
        // Ponto médio: ((5+15)/2=10.0, 10.0)
        val expected = COORDINATES(10, 10)
        val result = wallMidpoint(wall1, wall2)
        assertEquals(expected, result)
    }

    @Test
    fun `non-adjacent regions should throw exception`() {
        // wall1: posição (0, 0) com tamanho 10x10
        val wall1 = DENSITY(0, 0, AREA(10, 10), null,1.0)
        // wall2: não é adjacente, pois há um gap entre elas (ex: posição (20,20))
        val wall2 = DENSITY(20, 20, AREA(10, 10), null,1.0)

        // A função deve lançar IllegalArgumentException
        assertThrows(IllegalArgumentException::class.java) {
            wallMidpoint(wall1, wall2)
        }
    }
}
