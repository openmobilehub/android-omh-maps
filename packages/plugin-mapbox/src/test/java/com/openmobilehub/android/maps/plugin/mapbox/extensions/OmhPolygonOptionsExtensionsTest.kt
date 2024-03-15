package com.openmobilehub.android.maps.plugin.mapbox.extensions

import android.graphics.Color
import com.mapbox.maps.extension.style.layers.generated.FillLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class OmhPolygonOptionsExtensionsTest {
    private val lineLayer = mockk<LineLayer>(relaxed = true)
    private val fillLayer = mockk<FillLayer>(relaxed = true)
    private val scaleFactor = 1f
    private val logger = mockk<UnsupportedFeatureLogger>(relaxed = true)

    private val omhPolygonOptions = OmhPolygonOptions().apply {
        outline = listOf(
            OmhCoordinate(0.0, 0.0),
            OmhCoordinate(0.0, 20.0),
            OmhCoordinate(20.0, 20.0),
            OmhCoordinate(0.0, 20.0),
        )
        holes = listOf(
            listOf(
                OmhCoordinate(5.0, 5.0),
                OmhCoordinate(5.0, 10.0),
                OmhCoordinate(10.0, 10.0),
                OmhCoordinate(5.0, 10.0),
            )
        )
        clickable = true
        strokeColor = Color.RED
        fillColor = Color.BLUE
        strokeWidth = 100f
        isVisible = true
        zIndex = 10f
        strokeJointType = OmhJointType.ROUND
        strokePattern = listOf(
            OmhDot(),
            OmhGap(20f),
            OmhDash(30f)
        )
    }

    @Before
    fun setUp() {
        mockkObject(JoinTypeConverter)
    }

    @Test
    fun `applyPolygonOptions should apply polygon options to layers`() {
        // Arrange
        val lineJoin = mockk<LineJoin>()
        every { JoinTypeConverter.convertToLineJoin(any<Int>()) } returns lineJoin

        // Act
        omhPolygonOptions.applyPolygonOptions(lineLayer, fillLayer, scaleFactor, logger)

        // Assert
        verify { lineLayer.lineColor(Color.RED) }
        verify { fillLayer.fillColor(Color.BLUE) }
        verify { lineLayer.lineJoin(lineJoin) }
        verify { lineLayer.lineWidth(100.0) }
        verify { lineLayer.visibility(Visibility.VISIBLE) }
        verify { fillLayer.visibility(Visibility.VISIBLE) }
        verify { logger.logNotSupported("strokePattern") }
        verify { logger.logNotSupported("zIndex") }
    }
}
