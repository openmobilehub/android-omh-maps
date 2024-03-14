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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers

import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Polygon
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolygonClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.extensions.applyPolygonOptions
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IPolygonDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhPolygonImpl
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polygonLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.UUIDGenerator

class PolygonManager(
    private val mapView: MapView,
    private val scaleFactor: Float,
    private val uuidGenerator: UUIDGenerator = DefaultUUIDGenerator(),
    private val logger: UnsupportedFeatureLogger = polygonLogger
) : IPolygonDelegate {
    private var polygons = mutableMapOf<String, OmhPolygonImpl>()
    var clickListener: OmhOnPolygonClickListener? = null

    private fun generatePolygonId(): String {
        return POLYGON_LAYER_PREFIX + uuidGenerator.generate()
    }

    private fun generatePolygonOutlineId(polygonId: String): String {
        return POLYGON_OUTLINE_LAYER_PREFIX + polygonId
    }

    private fun getPolygonId(layerId: String): String {
        return layerId.replace(POLYGON_OUTLINE_LAYER_PREFIX, "")
    }

    fun maybeHandleClick(type: String, layerId: String): Boolean {
        if (type === POLYGON_LAYER_TYPE) {
            val omhPolygon = polygons[getPolygonId(layerId)]
            if (omhPolygon !== null && omhPolygon.getClickable()) {
                clickListener?.onPolygonClick(omhPolygon)
                return true
            }
        }

        return false
    }

    private fun getLineString(coordinates: List<OmhCoordinate>): LineString {
        val closedCoordinates = coordinates.toMutableList()
        closedCoordinates.add(coordinates.first())

        return LineString.fromLngLats(
            closedCoordinates.map { CoordinateConverter.convertToPoint(it) }
        )
    }

    private fun getPolygonFeature(
        outline: List<OmhCoordinate>,
        holes: List<List<OmhCoordinate>>?
    ): Feature {
        val outlineLineString = getLineString(outline)

        val holeLineStringList = holes?.map { hole ->
            getLineString(hole)
        } ?: emptyList()

        return Feature.fromGeometry(
            Polygon.fromOuterInner(
                outlineLineString,
                holeLineStringList
            )
        )
    }

    fun addPolygon(options: OmhPolygonOptions, style: Style?): OmhPolygon {
        val polygonId = generatePolygonId()
        val polygonOutlineId = generatePolygonOutlineId(polygonId)

        val source = geoJsonSource(polygonId) {
            feature(
                getPolygonFeature(options.outline, options.holes),
                polygonId
            )
        }

        val fillLayer = fillLayer(polygonId, polygonId) { }
        val outlineLayer = lineLayer(polygonOutlineId, polygonId) {
            lineMiterLimit(Constants.LINE_JOIN_MITER_LIMIT)
            lineRoundLimit(Constants.LINE_JOINT_ROUND_LIMIT)
        }
        options.applyPolygonOptions(outlineLayer, fillLayer, scaleFactor, logger)

        val omhPolygon = OmhPolygonImpl(
            source,
            fillLayer,
            outlineLayer,
            options,
            this.scaleFactor,
            this
        )

        polygons[polygonId] = omhPolygon

        style?.let { safeStyle ->
            omhPolygon.onStyleLoaded(safeStyle)
        }

        return omhPolygon
    }

    fun onStyleLoaded(style: Style) {
        polygons.forEach {
            it.value.onStyleLoaded(style)
        }
    }

    override fun updatePolygonSource(
        sourceId: String,
        outline: List<OmhCoordinate>,
        holes: List<List<OmhCoordinate>>?
    ) {
        mapView.mapboxMap.style?.let { style ->
            val feature = getPolygonFeature(outline, holes)
            val source = (style.getSource(sourceId) as GeoJsonSource)
            source.feature(feature)
        }
    }

    companion object {
        private const val POLYGON_LAYER_PREFIX = "polygon-"
        private const val POLYGON_OUTLINE_LAYER_PREFIX = "outline-"
        private const val POLYGON_LAYER_TYPE = "Polygon"
    }
}
