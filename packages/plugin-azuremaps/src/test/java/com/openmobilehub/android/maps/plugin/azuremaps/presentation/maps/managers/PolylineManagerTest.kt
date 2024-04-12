/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers

import com.azure.android.maps.control.ImageManager
import com.azure.android.maps.control.LayerManager
import com.azure.android.maps.control.PopupManager
import com.azure.android.maps.control.SourceManager
import com.azure.android.maps.control.source.DataSource
import com.mapbox.geojson.Feature
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolylineClickListener
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.uuid.UUIDGenerator
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.AzureMapInterface
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhPolylineImpl
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.util.UUID

class PolylineManagerTest {

    private val sourcesMock = mockk<SourceManager>(relaxed = true)
    private val layersMock = mockk<LayerManager>(relaxed = true)
    private val imagesMock = mockk<ImageManager>(relaxed = true)
    private val popupsMock = mockk<PopupManager>(relaxed = true)
    private val uuidGenerator = mockk<UUIDGenerator>()

    private lateinit var polylineManager: PolylineManager

    private val omhOnPolylineClickListener = mockk<OmhOnPolylineClickListener>(relaxed = true)

    @Before
    fun setUp() {
        every { uuidGenerator.generate() } returns UUID.fromString(DEFAULT_UUID)

        polylineManager = PolylineManager(
            object : AzureMapInterface {
                override val sources: SourceManager
                    get() = sourcesMock
                override val layers: LayerManager
                    get() = layersMock
                override val images: ImageManager
                    get() = imagesMock
                override val popups: PopupManager
                    get() = popupsMock
            },
            uuidGenerator
        ).apply {
            this.clickListener = omhOnPolylineClickListener
        }
    }

    @Test
    fun `addPolyline should add new polyline`() {
        // Act
        val polyline = polylineManager.addPolyline(OmhPolylineOptions())

        // Assert
        assertEquals(1, polylineManager.polylines.count())
        assertNotNull(polylineManager.polylines[polyline.id.toString()])
    }

    @Test
    fun `maybeHandleClick should call listener onPolylineClick for visible and clickable polyline`() {
        // Arrange
        val polyline = polylineManager.addPolyline(
            OmhPolylineOptions().apply {
                clickable = true
                isVisible = true
            }
        )

        // Act
        polylineManager.maybeHandleClick(polyline.id.toString())

        // Assert
        verify { omhOnPolylineClickListener.onPolylineClick(polyline) }
    }

    @Test
    fun `maybeHandleClick shouldn't call listener onPolylineClick if polyline is not visible`() {
        // Arrange
        val polyline = polylineManager.addPolyline(
            OmhPolylineOptions().apply {
                clickable = true
                isVisible = false
            }
        )

        // Act
        polylineManager.maybeHandleClick(polyline.id.toString())

        // Assert
        verify { omhOnPolylineClickListener wasNot Called }
    }

    @Test
    fun `maybeHandleClick shouldn't call listener onPolylineClick if polyline is not clickable`() {
        // Arrange
        val polyline = polylineManager.addPolyline(
            OmhPolylineOptions().apply {
                clickable = false
                isVisible = true
            }
        )

        // Act
        polylineManager.maybeHandleClick(polyline.id.toString())

        // Assert
        verify { omhOnPolylineClickListener wasNot Called }
    }

    @Test
    fun `maybeHandleClick shouldn't call listener onPolylineClick if polyline does not exist`() {
        // Arrange
        polylineManager.addPolyline(
            OmhPolylineOptions().apply {
                clickable = true
                isVisible = true
            }
        )

        // Act
        polylineManager.maybeHandleClick("1")

        // Assert
        verify { omhOnPolylineClickListener wasNot Called }
    }

    @Test
    fun `updatePolylineSourceWithPoints should update Feature in source`() {
        // Arrange
        val source = mockk<DataSource>(relaxed = true)
        val polylineUUID = UUID.randomUUID()

        // Act
        polylineManager.updatePolylineSourceWithPoints(polylineUUID, source, emptyList())

        // Assert
        verify { source.clear() }
        verify { source.add(any<Feature>()) }
    }

    @Test
    fun `removePolyline remove layer, source and polyline`() {
        // Arrange
        val id = UUID.fromString(DEFAULT_UUID)

        // Act
        polylineManager.addPolyline(OmhPolylineOptions())

        // Assert
        assertEquals(1, polylineManager.polylines.count())

        // Act
        polylineManager.removePolyline(id)

        // Assert
        verify { layersMock.remove(OmhPolylineImpl.getLineLayerID(id)) }
        verify { sourcesMock.remove(OmhPolylineImpl.getSourceID(id)) }

        assertEquals(0, polylineManager.polylines.count())
    }

    @Test
    fun `setScaleFactor should update polylines scale factors`() {
        // Arrange
        polylineManager.addPolyline(
            OmhPolylineOptions()
        ).apply {
            setScaleFactor(1.0f)
        }
        val newScaleFactor = 2.0f

        // Act
        polylineManager.scaleFactor = newScaleFactor

        // Assert
        polylineManager.polylines.values.forEach {
            assertEquals(newScaleFactor, it.scaleFactor)
        }
    }

    companion object {
        private const val DEFAULT_UUID = "00000000-0000-0000-0000-000000000000"
    }
}
