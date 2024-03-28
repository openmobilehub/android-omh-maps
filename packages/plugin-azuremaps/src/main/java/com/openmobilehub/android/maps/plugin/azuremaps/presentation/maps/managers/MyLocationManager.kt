package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.managers

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import com.azure.android.maps.control.AzureMap
import com.azure.android.maps.control.MapControl
import com.azure.android.maps.control.layer.BubbleLayer
import com.azure.android.maps.control.options.BubbleLayerOptions
import com.azure.android.maps.control.options.CameraOptions
import com.azure.android.maps.control.source.DataSource
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMyLocationButtonClickListener
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.utils.location.NativeLocationService
import com.openmobilehub.android.maps.core.utils.logging.Logger
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps.MyLocationControl
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.utils.commonLogger

class MyLocationManager(
    context: Context,
    private val mapControl: MapControl,
    private val mapView: AzureMap,
    private val nativeLocationService: NativeLocationService = NativeLocationService(context),
    private val myLocationControl: MyLocationControl = MyLocationControl(context),
    private val logger: Logger = commonLogger
) {
    private var myLocationEnabled = false
    private var myLocationControlAdded = false
    private var lastLocation: OmhCoordinate? = null

    private val dataSource = DataSource()
    private val layer = BubbleLayer(
        dataSource,
        BubbleLayerOptions.bubbleColor(BUBBLE_COLOR),
        BubbleLayerOptions.bubbleRadius(BUBBLE_RADIUS)
    )

    init {
        mapView.sources.add(dataSource)
    }

    private fun centerOnCurrentLocation() {
        if (lastLocation != null) {
            mapView.setCamera(
                CameraOptions.center(lastLocation!!.latitude, lastLocation!!.longitude)
            )
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    fun setMyLocationEnabled(enable: Boolean) {
        myLocationEnabled = enable

        if (enable) {
            mapView.layers.add(layer)

            nativeLocationService.startLocationUpdates(
                onLocationUpdateSuccess = { omhCoordinate ->
                    lastLocation = omhCoordinate
                    val updatedFeature = Feature.fromGeometry(
                        Point.fromLngLat(
                            omhCoordinate.longitude,
                            omhCoordinate.latitude
                        )
                    )
                    dataSource.setShapes(updatedFeature)

                    if (!myLocationControlAdded) {
                        mapControl.addView(myLocationControl)
                        myLocationControlAdded = true
                    }
                },
                onLocationUpdateFailure = { exception ->
                    logger.logError("Failed to update location: ${exception.message}")
                }
            )
        } else {
            mapView.layers.remove(layer)
            nativeLocationService.stopLocationUpdates()
            mapControl.removeView(myLocationControl)
            myLocationControlAdded = false
        }
    }

    fun isMyLocationEnabled(): Boolean {
        return myLocationEnabled
    }

    fun setMyLocationButtonClickListener(
        omhOnMyLocationButtonClickListener: OmhOnMyLocationButtonClickListener
    ) {
        myLocationControl.setOnClickListener {
            omhOnMyLocationButtonClickListener.onMyLocationButtonClick()
            centerOnCurrentLocation()
        }
    }

    companion object {
        private const val BUBBLE_COLOR = "#4a91e2"
        private const val BUBBLE_RADIUS = 7.0f
    }
}
