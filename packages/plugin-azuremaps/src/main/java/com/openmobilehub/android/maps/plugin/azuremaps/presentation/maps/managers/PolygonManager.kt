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

import androidx.annotation.VisibleForTesting
import com.azure.android.maps.control.layer.LineLayer
import com.azure.android.maps.control.layer.PolygonLayer
import com.azure.android.maps.control.source.DataSource
import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Polygon
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolygonClickListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.core.utils.uuid.UUIDGenerator
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.AzureMapInterface
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IPolygonDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhPolygonImpl
import com.openmobilehub.android.maps.plugin.azuremaps.utils.Constants
import com.openmobilehub.android.maps.plugin.azuremaps.utils.CoordinateConverter
import java.util.UUID

internal class PolygonManager(
    private val map: AzureMapInterface,
    private val uuidGenerator: UUIDGenerator = DefaultUUIDGenerator()
) : IPolygonDelegate {

    private val _polygons = mutableMapOf<String, OmhPolygonImpl>()

    @VisibleForTesting
    internal val polygons: Map<String, OmhPolygonImpl> = _polygons
    internal var clickListener: OmhOnPolygonClickListener? = null

    fun addPolygon(options: OmhPolygonOptions): OmhPolygonImpl {
        val polygonId = uuidGenerator.generate()

        val source = DataSource(OmhPolygonImpl.getSourceID(polygonId))

        map.sources.add(source)

        source.add(getPolygonFeature(polygonId, options.outline, options.holes))

        val polygonLayer = PolygonLayer(
            source,
            OmhPolygonImpl.getPolygonLayerID(polygonId)
        )
        val lineLayer = LineLayer(source, OmhPolygonImpl.getPolygonLineLayerID(polygonId))

        val omhPolygon = OmhPolygonImpl(polygonId, source, polygonLayer, lineLayer, this, options)

        map.layers.add(polygonLayer)
        map.layers.add(lineLayer)

        _polygons[polygonId.toString()] = omhPolygon

        return omhPolygon
    }

    override fun updatePolygonSourceWithOutline(
        polygonId: UUID,
        source: DataSource,
        outline: List<OmhCoordinate>,
        holes: List<List<OmhCoordinate>>?
    ) {
        source.clear()
        source.add(getPolygonFeature(polygonId, outline, holes))
    }

    private fun getPolygonFeature(
        polygonId: UUID,
        outline: List<OmhCoordinate>,
        holes: List<List<OmhCoordinate>>?
    ): Feature {
        val polygonFeatureProps = JsonObject().apply {
            addProperty(Constants.POLYGON_FEATURE_UUID_BINDING, polygonId.toString())
        }

        if (holes.isNullOrEmpty()) {
            return Feature.fromGeometry(
                Polygon.fromLngLats(
                    listOf(outline.map { CoordinateConverter.convertToPoint(it) })
                ),
                polygonFeatureProps
            )
        }

        val outlineLineString = getLineString(outline)
        val holeLineStringList = holes.map { hole ->
            getLineString(hole)
        }

        return Feature.fromGeometry(
            Polygon.fromOuterInner(
                outlineLineString,
                holeLineStringList
            ),
            polygonFeatureProps
        )
    }

    private fun getLineString(coordinates: List<OmhCoordinate>): LineString {
        val closedCoordinates = coordinates.toMutableList()
        closedCoordinates.add(coordinates.first())

        return LineString.fromLngLats(
            closedCoordinates.map { CoordinateConverter.convertToPoint(it) }
        )
    }

    fun maybeHandleClick(polygonId: String): Boolean {
        val polygon = _polygons[polygonId]

        if (polygon == null || !polygon.getClickable() || !polygon.isVisible()) {
            return false
        }

        return clickListener?.onPolygonClick(polygon) ?: false
    }
}
