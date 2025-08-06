package domainTests.areaValidationTests

import domain.doTheSubRegionsCoverAllArea
import model.AREA
import model.DENSITY
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DoTheSubRegionsCoverAllAreaTest {

    @Test
    fun `should return true when sub-regions cover the whole area exactly`() {
        val building = AREA(10, 10)
        val subRegions = listOf(
            DENSITY(0, 0, AREA(5, 10), null,1.0),
            DENSITY(5, 0, AREA(5, 10), null,1.0)
        )
        assertTrue(doTheSubRegionsCoverAllArea(building, subRegions))
    }

    @Test
    fun `should return false when sub-regions do not cover entire area`() {
        val building = AREA(10, 10)
        val subRegions = listOf(
            DENSITY(0, 0, AREA(5, 10), null,1.0) // só metade
        )
        assertFalse(doTheSubRegionsCoverAllArea(building, subRegions))
    }

    @Test
    fun `should return false when sub-regions exceed the building area`() {
        val building = AREA(10, 10)
        val subRegions = listOf(
            DENSITY(0, 0, AREA(10, 10), null,1.0),
            DENSITY(0, 0, AREA(1, 1), null,1.0) // área a mais
        )
        assertFalse(doTheSubRegionsCoverAllArea(building, subRegions))
    }

    @Test
    fun `should return false when there are no sub-regions`() {
        val building = AREA(10, 10)
        val subRegions = emptyList<DENSITY>()
        assertFalse(doTheSubRegionsCoverAllArea(building, subRegions))
    }

    @Test
    fun `should return true for single sub-region that matches building exactly`() {
        val building = AREA(10, 10)
        val subRegions = listOf(
            DENSITY(0, 0, AREA(10, 10), null,1.0)
        )
        assertTrue(doTheSubRegionsCoverAllArea(building, subRegions))
    }
}
