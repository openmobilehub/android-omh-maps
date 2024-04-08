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

import com.azure.android.maps.control.layer.LineLayer
import com.azure.android.maps.control.source.DataSource
import com.google.gson.JsonObject
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolylineClickListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolylineOptions
import com.openmobilehub.android.maps.core.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.core.utils.uuid.UUIDGenerator
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.AzureMapInterface
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IPolylineDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.OmhPolylineImpl
import com.openmobilehub.android.maps.plugin.azuremaps.utils.Constants
import com.openmobilehub.android.maps.plugin.azuremaps.utils.CoordinateConverter
import java.util.UUID

internal class PolylineManager(
    private val map: AzureMapInterface,
    private val uuidGenerator: UUIDGenerator = DefaultUUIDGenerator()
) : IPolylineDelegate {

    internal var clickListener: OmhOnPolylineClickListener? = null
    private val _polylines = mutableMapOf<String, OmhPolylineImpl>()
    internal val polylines: Map<String, OmhPolylineImpl> = _polylines

    fun addPolyline(options: OmhPolylineOptions): OmhPolylineImpl {
        val polylineId = uuidGenerator.generate()

        val source = DataSource(OmhPolylineImpl.getSourceID(polylineId))

        map.sources.add(source)

        source.add(getPolylineFeature(polylineId, options.points))

        val layer = LineLayer(
            source,
            OmhPolylineImpl.getLineLayerID(polylineId)
        )

        val omhPolyline = OmhPolylineImpl(polylineId, source, layer, this, options)

        map.layers.add(layer)

        _polylines[polylineId.toString()] = omhPolyline

        return omhPolyline
    }

    override fun updatePolylineSourceWithPoints(
        polylineId: UUID,
        source: DataSource,
        points: List<OmhCoordinate>
    ) {
        source.clear()
        source.add(getPolylineFeature(polylineId, points))
    }

    private fun getPolylineFeature(
        polylineId: UUID,
        points: List<OmhCoordinate>,
    ): Feature {
        val polylineFeatureProps = JsonObject().apply {
            addProperty(Constants.POLYLINE_FEATURE_UUID_BINDING, polylineId.toString())
        }

        return Feature.fromGeometry(
            LineString.fromLngLats(
                points.map { CoordinateConverter.convertToPoint(it) },
            ),
            polylineFeatureProps
        )
    }

    fun maybeHandleClick(polylineId: String): Boolean {
        val polyline = _polylines[polylineId]

        if (polyline == null || !polyline.getClickable() || !polyline.isVisible()) {
            return false
        }

        return clickListener?.onPolylineClick(polyline) ?: false
    }
}
