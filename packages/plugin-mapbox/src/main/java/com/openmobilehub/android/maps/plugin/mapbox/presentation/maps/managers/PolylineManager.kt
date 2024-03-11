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
import com.mapbox.maps.extension.style.layers.Layer
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.Source
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolylineClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.extensions.applyPolylineOptions
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhPolylineImpl
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.PolylineDelegate
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polylineLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.UUIDGenerator

class PolylineManager(
    private val mapView: MapView,
    private val scaleFactor: Float,
    private val uuidGenerator: UUIDGenerator = DefaultUUIDGenerator(),
    private val logger: UnsupportedFeatureLogger = polylineLogger
) : PolylineDelegate {
    private var polylines = mutableMapOf<String, OmhPolylineImpl>()
    private var queue = mutableListOf<Pair<Source, Layer>>()
    var clickListener: OmhOnPolylineClickListener? = null

    fun getQueue(): List<Pair<Source, Layer>> {
        return queue
    }

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

    fun maybeHandleClick(type: String, layerId: String) {
        if (type === POLYLINE_LAYER_TYPE) {
            val omhPolyline = polylines[layerId]
            if (omhPolyline !== null && omhPolyline.getClickable()) {
                clickListener?.onPolylineClick(omhPolyline)
            }
        }
    }

    fun addPolyline(options: OmhPolylineOptions, style: Style?): OmhPolyline {
        val polylineId = generatePolylineId()

        val source = geoJsonSource(polylineId) {
            feature(
                getPolylineFeature(options.points),
                polylineId
            )
        }

        val layer = lineLayer(polylineId, polylineId) { }
        options.applyPolylineOptions(layer, scaleFactor, logger)

        val omhPolyline = OmhPolylineImpl(
            style,
            layer,
            options,
            scaleFactor,
            this
        )

        polylines[polylineId] = omhPolyline

        style?.let { safeStyle ->
            safeStyle.addSource(source)
            safeStyle.addLayer(layer)
        } ?: queue.add(Pair(source, layer))

        return omhPolyline
    }

    fun onStyleLoaded(style: Style) {
        queue.forEach { (source, layer) ->
            style.addSource(source)
            style.addLayer(layer)
        }

        polylines.forEach {
            it.value.applyBufferedProperties(style)
        }

        queue.clear()
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
        private const val POLYLINE_LAYER_TYPE = "LineString"
    }
}
