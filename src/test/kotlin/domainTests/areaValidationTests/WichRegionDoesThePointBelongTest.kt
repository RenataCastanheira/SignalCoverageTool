package domainTests.areaValidationTests

import domain.whichRegionDoesThePointBelong
import model.AREA
import model.COORDINATES
import model.DENSITY
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class WichRegionDoesThePointBelongTest {

    @Test
    fun `point inside a single sub-region should return that region`() {
        val region = DENSITY(0, 0, AREA(10, 10), null,1.0)
        val point = COORDINATES(5, 5)
        val result = whichRegionDoesThePointBelong(point, listOf(region))
        assertEquals(region, result)
    }

    @Test
    fun `point outside all sub-regions should return null`() {
        val region = DENSITY(0, 0, AREA(10, 10), null,1.0)
        val point = COORDINATES(15, 15)
        val result = whichRegionDoesThePointBelong(point, listOf(region))
        assertNull(result)
    }

    @Test
    fun `point on the edge of a region should be considered inside`() {
        val region = DENSITY(0, 0, AREA(10, 10), null,1.0)
        val point = COORDINATES(10, 10) // exactly on the bottom-right corner
        val result = whichRegionDoesThePointBelong(point, listOf(region))
        assertEquals(region, result)
    }

    @Test
    fun `point inside second region should return that one`() {
        val region1 = DENSITY(0, 0, AREA(5, 5), null,1.0)
        val region2 = DENSITY(10, 10, AREA(5, 5), null,1.0)
        val point = COORDINATES(12, 12)
        val result = whichRegionDoesThePointBelong(point, listOf(region1, region2))
        assertEquals(region2, result)
    }

    @Test
    fun `point inside overlapping regions should return the first match`() {
        val region1 = DENSITY(0, 0, AREA(15, 15), null,1.0)
        val region2 = DENSITY(10, 10, AREA(10, 10), null,1.0)
        val point = COORDINATES(12, 12)
        val result = whichRegionDoesThePointBelong(point, listOf(region1, region2))
        assertEquals(region1, result)
    }

    @Test
    fun `empty list of regions should return null`() {
        val point = COORDINATES(5, 5)
        val result = whichRegionDoesThePointBelong(point, emptyList())
        assertNull(result)
    }
}
