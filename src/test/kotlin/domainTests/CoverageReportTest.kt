package domainTests

import model.AREA
import model.COORDINATES
import model.DENSITY
import org.example.domain.buildCoverageReport
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File
import kotlin.test.Test

class CoverageReportTest {

    private fun fakeSubregions(): List<DENSITY> {
        val area = AREA(100, 100)
        return listOf(
            DENSITY(x = 0, y = 0, subRegionArea = area, adjacencyRegionsIndexes = listOf(1), value = 1.0),
            DENSITY(x = 100, y = 0, subRegionArea = area, adjacencyRegionsIndexes = listOf(0, 2), value = 1.0),
            DENSITY(x = 200, y = 0, subRegionArea = area, adjacencyRegionsIndexes = listOf(1), value = 1.0)
        )
    }

    @Test
    fun `testa geracao de relatorio com 1 AP`() {
        val subregions = fakeSubregions()
        val area = AREA(300, 100)
        val aps = listOf(COORDINATES(0, 0))
        val output = "test_coverage_report.txt"

        val results = buildCoverageReport(subregions, aps, area, output)

        // Confirma que o ficheiro foi criado
        val file = File(output)
        assertTrue(file.exists())
        assertTrue(file.readText().contains("AP(0, 0):"))

        // Opcional: confirmar que alguns valores foram calculados
        // (como o target mais à direita, por exemplo)
        assertTrue(file.readText().contains("Target(200, 0"))
    }
}