package com.openmobilehub.android.maps.plugin.mapbox.extensions

import android.graphics.Color
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.presentation.models.OmhJointType
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanGradient
import com.openmobilehub.android.maps.core.presentation.models.OmhStyleSpanMonochromatic
import com.openmobilehub.android.maps.core.utils.ScreenUnitConverter
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.CapConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.JoinTypeConverter
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class OmhPolylineOptionsExtensionsTest {
    private val lineLayer = mockk<LineLayer>(relaxed = true)
    private val logger = mockk<UnsupportedFeatureLogger>(relaxed = true)

    private val omhPolylineOptions = OmhPolylineOptions().apply {
        points = listOf(
            OmhCoordinate(20.0, 20.0),
            OmhCoordinate(10.0, 10.0),
        )
        clickable = true
        color = Color.RED
        width = 10f
        isVisible = true
        zIndex = 10f
        jointType = OmhJointType.ROUND
        pattern = listOf(
            OmhDot(),
            OmhGap(10f),
            OmhDash(20f),
        )
        startCap = OmhRoundCap()
        endCap = OmhSquareCap()
        cap = OmhSquareCap()
        spans = listOf(
            OmhStyleSpanMonochromatic(Color.RED),
            OmhStyleSpanGradient(
                Color.RED,
                Color.BLUE,
            )
        )
    }

    @Before
    fun setUp() {
        mockkObject(JoinTypeConverter)
        mockkObject(CapConverter)
        mockkObject(ScreenUnitConverter)
    }

    @Test
    fun `applyPolylineOptions should apply polyline options to layer`() {
        // Arrange
        val lineJoin = mockk<LineJoin>()
        every { JoinTypeConverter.convertToLineJoin(any<Int>()) } returns lineJoin

        val lineCap = mockk<LineCap>()
        every { CapConverter.convertToLineCap(any<OmhSquareCap>()) } returns lineCap

        val density = 2.0f
        every { ScreenUnitConverter.pxToDp(any<Float>()) } answers { firstArg<Float>() / density }

        // Act
        omhPolylineOptions.applyPolylineOptions(lineLayer, logger)

        // Assert
        verify { lineLayer.lineColor(Color.RED) }
        verify { lineLayer.lineWidth((omhPolylineOptions.width!! / density).toDouble()) }
        verify { lineLayer.lineJoin(lineJoin) }
        verify { lineLayer.visibility(Visibility.VISIBLE) }
        verify { lineLayer.lineCap(lineCap) }

        verify { logger.logNotSupported("endCap") }
        verify { logger.logNotSupported("pattern") }
        verify { logger.logNotSupported("spans") }
        verify { logger.logNotSupported("startCap") }
        verify { logger.logNotSupported("zIndex") }
    }
}
