package domainTests.areaValidationTests

import domain.areTheAreasOverlapping
import model.AREA
import model.DENSITY
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class AreTheAreasOverlappingTest {

    /*
    @Test
    fun `sub-region with zero size should be considered overlapping`() {

        val buildingArea = AREA(100, 100)

        val subRegionsAreas = mutableListOf<DENSITY>()


        subRegionsAreas.add(DENSITY(10, 10, AREA(0, 0),null ,1.0))

        assertTrue(areTheAreasOverlapping(buildingArea, subRegionsAreas))

    }

    @Test
    fun `sub-region outside building limits should be considered overlapping`() {

        val buildingArea = AREA(100, 100)

        val subRegionsAreas = mutableListOf<DENSITY>()


        subRegionsAreas.add(DENSITY(90, 90, AREA(20, 20), null,1.0))

        assertTrue(areTheAreasOverlapping(buildingArea, subRegionsAreas))

    }

     */

    @Test
    fun `two sub-regions that overlap should be considered overlapping`() {

        val buildingArea = AREA(100, 100)

        val subRegionsAreas = mutableListOf<DENSITY>()


        subRegionsAreas.add(DENSITY(10, 10, AREA(20, 20), null,1.0))

        subRegionsAreas.add(DENSITY(15, 15, AREA(20, 20), null,1.0))

        assertTrue(areTheAreasOverlapping(buildingArea, subRegionsAreas))

    }

    @Test
    fun `two separate sub-regions should not be considered overlapping`() {

        val buildingArea = AREA(100, 100)

        val subRegionsAreas = mutableListOf<DENSITY>()


        subRegionsAreas.add(DENSITY(10, 10, AREA(20, 20), null,1.0))

        subRegionsAreas.add(DENSITY(40, 40, AREA(20, 20), null,1.0))

        assertFalse(areTheAreasOverlapping(buildingArea, subRegionsAreas))

    }

    @Test
    fun `multiple non-overlapping sub-regions should not be considered overlapping`() {

        val buildingArea = AREA(100, 100)

        val subRegionsAreas = mutableListOf<DENSITY>()


        subRegionsAreas.add(DENSITY(10, 10, AREA(20, 20), null,1.0))

        subRegionsAreas.add(DENSITY(50, 50, AREA(20, 20), null,1.0))

        subRegionsAreas.add(DENSITY(80, 80, AREA(10, 10), null,1.0))

        assertFalse(areTheAreasOverlapping(buildingArea, subRegionsAreas))

    }
}
