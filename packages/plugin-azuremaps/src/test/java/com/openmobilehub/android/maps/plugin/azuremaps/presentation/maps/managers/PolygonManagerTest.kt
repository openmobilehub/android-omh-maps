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
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolygonClickListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.uuid.UUIDGenerator
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.AzureMapInterface
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhPolygonImpl
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.util.UUID

class PolygonManagerTest {

    private val sourcesMock = mockk<SourceManager>(relaxed = true)
    private val layersMock = mockk<LayerManager>(relaxed = true)
    private val imagesMock = mockk<ImageManager>(relaxed = true)
    private val popupsMock = mockk<PopupManager>(relaxed = true)
    private val uuidGenerator = mockk<UUIDGenerator>()

    private lateinit var polygonManager: PolygonManager

    private val omhOnPolygonClickListener = mockk<OmhOnPolygonClickListener>(relaxed = true)

    private val outline = listOf(
        OmhCoordinate(0.0, 0.0),
        OmhCoordinate(0.0, 20.0),
        OmhCoordinate(20.0, 20.0),
        OmhCoordinate(0.0, 20.0),
    )

    private val holes = listOf(
        listOf(
            OmhCoordinate(5.0, 5.0),
            OmhCoordinate(5.0, 10.0),
            OmhCoordinate(10.0, 10.0),
            OmhCoordinate(5.0, 10.0),
        )
    )

    @Before
    fun setUp() {
        every { uuidGenerator.generate() } returns UUID.fromString(DEFAULT_UUID)

        polygonManager = PolygonManager(
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
            this.clickListener = omhOnPolygonClickListener
        }
    }

    @Test
    fun `addPolygon should add new polygon`() {
        // Act
        val polygon = polygonManager.addPolygon(OmhPolygonOptions())

        // Assert
        assertEquals(1, polygonManager.polygons.count())
        assertNotNull(polygonManager.polygons[polygon.id.toString()])
    }

    @Test
    fun `maybeHandleClick should call listener onPolygonClick for visible and clickable polygon`() {
        // Arrange
        val polygon = polygonManager.addPolygon(
            OmhPolygonOptions().apply {
                clickable = true
                isVisible = true
            }
        )

        // Act
        polygonManager.maybeHandleClick(polygon.id.toString())

        // Assert
        verify { omhOnPolygonClickListener.onPolygonClick(polygon) }
    }

    @Test
    fun `maybeHandleClick shouldn't call listener onPolygonClick if polygon is not visible`() {
        // Arrange
        val polygon = polygonManager.addPolygon(
            OmhPolygonOptions().apply {
                clickable = true
                isVisible = false
            }
        )

        // Act
        polygonManager.maybeHandleClick(polygon.id.toString())

        // Assert
        verify { omhOnPolygonClickListener wasNot Called }
    }

    @Test
    fun `maybeHandleClick shouldn't call listener onPolygonClick if polygon is not clickable`() {
        // Arrange
        val polygon = polygonManager.addPolygon(
            OmhPolygonOptions().apply {
                clickable = false
                isVisible = true
            }
        )

        // Act
        polygonManager.maybeHandleClick(polygon.id.toString())

        // Assert
        verify { omhOnPolygonClickListener wasNot Called }
    }

    @Test
    fun `maybeHandleClick shouldn't call listener onPolygonClick if polygon does not exist`() {
        // Arrange
        polygonManager.addPolygon(
            OmhPolygonOptions().apply {
                clickable = true
                isVisible = true
            }
        )

        // Act
        polygonManager.maybeHandleClick("1")

        // Assert
        verify { omhOnPolygonClickListener wasNot Called }
    }

    @Test
    fun `updatePolygonSourceWithPoints should update Feature in source when holes are null`() {
        // Arrange
        val source = mockk<DataSource>(relaxed = true)
        val polygonUUID = UUID.randomUUID()

        // Act
        polygonManager.updatePolygonSourceWithOutline(polygonUUID, source, outline, null)

        // Assert
        verify { source.clear() }
        verify { source.add(any<Feature>()) }
    }

    @Test
    fun `updatePolygonSourceWithPoints should update Feature in source when holes are empty`() {
        // Arrange
        val source = mockk<DataSource>(relaxed = true)
        val polygonUUID = UUID.randomUUID()

        // Act
        polygonManager.updatePolygonSourceWithOutline(polygonUUID, source, outline, emptyList())

        // Assert
        verify { source.clear() }
        verify { source.add(any<Feature>()) }
    }

    @Test
    fun `updatePolygonSourceWithPoints should update Feature in source when holes are provided`() {
        // Arrange
        val source = mockk<DataSource>(relaxed = true)
        val polygonUUID = UUID.randomUUID()

        // Act
        polygonManager.updatePolygonSourceWithOutline(polygonUUID, source, outline, holes)

        // Assert
        verify { source.clear() }
        verify { source.add(any<Feature>()) }
    }

    @Test
    fun `removePolygon remove layers, source and polygon`() {
        // Arrange
        val id = UUID.fromString(DEFAULT_UUID)

        // Act
        polygonManager.addPolygon(OmhPolygonOptions())

        // Assert
        assertEquals(1, polygonManager.polygons.count())

        // Act
        polygonManager.removePolygon(id)

        // Assert
        verify { layersMock.remove(OmhPolygonImpl.getPolygonLineLayerID(id)) }
        verify { layersMock.remove(OmhPolygonImpl.getPolygonLayerID(id)) }
        verify { sourcesMock.remove(OmhPolygonImpl.getSourceID(id)) }

        assertEquals(0, polygonManager.polygons.count())
    }

    companion object {
        private const val DEFAULT_UUID = "00000000-0000-0000-0000-000000000000"
    }
}
