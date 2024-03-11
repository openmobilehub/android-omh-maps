package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.managers

import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Polygon
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.Layer
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.sources.Source
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnPolygonClickListener
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolygon
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPolygonOptions
import com.openmobilehub.android.maps.core.utils.logging.UnsupportedFeatureLogger
import com.openmobilehub.android.maps.plugin.mapbox.extensions.applyPolygonOptions
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.OmhPolygonImpl
import com.openmobilehub.android.maps.plugin.mapbox.presentation.maps.PolygonDelegate
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.polygonLogger
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.UUIDGenerator

class PolygonManager(
    private val mapView: MapView,
    private val scaleFactor: Float,
    private val uuidGenerator: UUIDGenerator = DefaultUUIDGenerator(),
    private val logger: UnsupportedFeatureLogger = polygonLogger
) : PolygonDelegate {
    private var polygons = mutableMapOf<String, OmhPolygonImpl>()
    private var queue = mutableListOf<Pair<Source, List<Layer>>>()
    var clickListener: OmhOnPolygonClickListener? = null

    fun getQueue(): List<Pair<Source, List<Layer>>> {
        return queue
    }

    private fun generatePolygonId(): String {
        return POLYGON_LAYER_PREFIX + uuidGenerator.generate()
    }

    private fun generatePolygonOutlineId(polygonId: String): String {
        return POLYGON_OUTLINE_LAYER_PREFIX + polygonId
    }

    private fun getPolygonId(layerId: String): String {
        return layerId.replace(POLYGON_OUTLINE_LAYER_PREFIX, "")
    }

    fun maybeHandleClick(type: String, layerId: String) {
        if (type === POLYGON_LAYER_TYPE) {
            val omhPolygon = polygons[getPolygonId(layerId)]
            if (omhPolygon !== null && omhPolygon.getClickable()) {
                clickListener?.onPolygonClick(omhPolygon)
            }
        }
    }

    fun addPolygon(options: OmhPolygonOptions, style: Style?): OmhPolygon {
        val polygonId = generatePolygonId()
        val polygonOutlineId = generatePolygonOutlineId(polygonId)

        val closedOutline = options.outline.toMutableList()
        closedOutline.add(options.outline.first())

        val outlineLineString = LineString.fromLngLats(
            closedOutline.map { CoordinateConverter.convertToPoint(it) }
        )

        val holeLineStringList = options.holes?.map { hole ->
            val closedHole = hole.toMutableList()
            closedHole.add(hole.first())

            LineString.fromLngLats(
                closedHole.map { CoordinateConverter.convertToPoint(it) }
            )
        }

        val source = geoJsonSource(polygonId) {
            feature(
                Feature.fromGeometry(
                    Polygon.fromOuterInner(
                        outlineLineString,
                        holeLineStringList ?: emptyList()
                    )
                ),
                polygonId
            )
        }

        val fillLayer = fillLayer(polygonId, polygonId) {}
        val outlineLayer = lineLayer(polygonOutlineId, polygonId) { }
        options.applyPolygonOptions(outlineLayer, fillLayer, scaleFactor, logger)

        val omhPolygon = OmhPolygonImpl(
            style,
            fillLayer,
            outlineLayer,
            options,
            this.scaleFactor,
            this
        )

        polygons[polygonId] = omhPolygon

        style?.let { safeStyle ->
            safeStyle.addSource(source)
            safeStyle.addLayer(fillLayer)
            safeStyle.addLayer(outlineLayer)
        } ?: queue.add(Pair(source, listOf(fillLayer, outlineLayer)))

        return omhPolygon
    }

    fun onStyleLoaded(style: Style) {
        queue.forEach { (source, layers) ->
            style.addSource(source)
            layers.forEach { layer -> style.addLayer(layer) }
        }

        polygons.forEach {
            it.value.applyBufferedProperties(style)
        }

        queue.clear()
    }

    override fun updatePolygonSource(
        sourceId: String,
        outline: List<OmhCoordinate>,
        holes: List<List<OmhCoordinate>>?
    ) {
        val closedOutline = outline.toMutableList()
        closedOutline.add(outline.first())

        val outlineLineString = LineString.fromLngLats(
            closedOutline.map { CoordinateConverter.convertToPoint(it) }
        )

        val holeLineStringList = holes?.map { hole ->
            val closedHole = hole.toMutableList()
            closedHole.add(hole.first())

            LineString.fromLngLats(
                closedHole.map { CoordinateConverter.convertToPoint(it) }
            )
        }

        mapView.mapboxMap.style?.let { style ->
            val feature =
                Feature.fromGeometry(
                    Polygon.fromOuterInner(
                        outlineLineString,
                        holeLineStringList ?: emptyList()
                    )
                )
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
