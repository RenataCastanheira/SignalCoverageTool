import org.example.domain.Config
import org.example.domain.Config.dRef
import org.example.domain.propagation.*
import kotlin.math.log10

var results  = mutableListOf<Double>()

fun propagationValues (node: PropagationTree): List<Double> {

    when (node) {

        is Leaf -> {

            val distLoss = if (node.totalDist >0.0) Config.atenInDistRef + 10 * Config.n * log10(node.totalDist / dRef) else 0.0
            val propagation  = node.propValue + distLoss
            node.propValue = propagation
            results.add(propagation)

        }

        is Node -> {

            node.children.forEach {
                propagationValues(it)
            }

        }
    }

    return results.toList()
}


var minValue : Double? = null

fun findMinAtenuationValue(node: PropagationTree): Double? {

    when (node) {

        is Leaf -> {

            val value  = node.propValue
            if (minValue == null || value < minValue!!) {
                minValue = value
            }

        }
        is Node -> {

            node.children.forEach { findMinAtenuationValue(node)}

        }
    }

    return minValue

}