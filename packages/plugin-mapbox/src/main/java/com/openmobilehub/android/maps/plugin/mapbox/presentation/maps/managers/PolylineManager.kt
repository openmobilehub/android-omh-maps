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
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolylineClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.extensions.applyPolylineOptions
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IPolylineDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhPolylineImpl
import com.openmobilehub.android.maps.plugin.mapbox.utils.Constants
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polylineLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.UUIDGenerator

class PolylineManager(
    private val mapView: MapView,
    private val scaleFactor: Float,
    private val uuidGenerator: UUIDGenerator = DefaultUUIDGenerator(),
    private val logger: UnsupportedFeatureLogger = polylineLogger
) : IPolylineDelegate {
    private var polylines = mutableMapOf<String, OmhPolylineImpl>()
    var clickListener: OmhOnPolylineClickListener? = null

    private fun generatePolylineId(): String {
        return POLYLINE_LAYER_PREFIX + uuidGenerator.generate()
    }

    private fun getPolylineFeature(
        points: List<OmhCoordinate>,
    ): Feature {
        return Feature.fromGeometry(
            LineString.fromLngLats(
                points.map { CoordinateConverter.convertToPoint(it) }
            )
        )
    }

    fun maybeHandleClick(type: String, layerId: String): Boolean {
        if (type === Constants.POLYLINE_LAYER_TYPE) {
            val omhPolyline = polylines[layerId]
            if (omhPolyline !== null && omhPolyline.getClickable()) {
                clickListener?.onPolylineClick(omhPolyline)?.let { eventConsumed ->
                    return eventConsumed
                }
            }
        }
        return false
    }

    fun addPolyline(options: OmhPolylineOptions, style: Style?): OmhPolyline {
        val polylineId = generatePolylineId()

        val source = geoJsonSource(polylineId) {
            feature(
                getPolylineFeature(options.points),
                polylineId
            )
        }

        val layer = lineLayer(polylineId, polylineId) {
            lineMiterLimit(Constants.LINE_JOIN_MITER_LIMIT)
            lineRoundLimit(Constants.LINE_JOINT_ROUND_LIMIT)
        }
        options.applyPolylineOptions(layer, scaleFactor, logger)

        val omhPolyline = OmhPolylineImpl(
            source,
            layer,
            options,
            scaleFactor,
            this
        )

        polylines[polylineId] = omhPolyline

        style?.let { safeStyle ->
            omhPolyline.onStyleLoaded(safeStyle)
        }

        return omhPolyline
    }

    fun onStyleLoaded(style: Style) {
        polylines.forEach {
            it.value.onStyleLoaded(style)
        }
    }

    override fun updatePolylinePoints(sourceId: String, points: List<OmhCoordinate>) {
        mapView.mapboxMap.style?.let { style ->
            val feature = getPolylineFeature(points)
            val source = (style.getSource(sourceId) as GeoJsonSource)
            source.feature(feature)
        }
    }

    companion object {
        private const val POLYLINE_LAYER_PREFIX = "polyline-"
    }
}
