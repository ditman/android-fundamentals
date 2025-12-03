package engineer.filip.hoarder.ui.home

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath

class RoundedPolygonShape(private val roundedPolygon: RoundedPolygon) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = roundedPolygon.toPath().asComposePath()
        // Scale and translate the path to fit the given size
        val matrix = android.graphics.Matrix()
        val bounds =
            roundedPolygon.calculateBounds()
                .let { Rect(Offset(it[0], it[1]), Offset(it[2], it[3])) }
        val scaleX = size.width / bounds.width
        val scaleY = size.height / bounds.height
        matrix.setScale(scaleX, scaleY)
        matrix.postTranslate(-bounds.left * scaleX, -bounds.top * scaleY)
        path.asAndroidPath().transform(matrix)

        return Outline.Generic(path)
    }
}

