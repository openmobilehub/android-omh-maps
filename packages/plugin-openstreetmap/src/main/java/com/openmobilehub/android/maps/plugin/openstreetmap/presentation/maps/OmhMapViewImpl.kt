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

package com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageButton
import androidx.preference.PreferenceManager
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMapView
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhOnMapReadyCallback
import com.openmobilehub.android.maps.plugin.openstreetmap.R
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.Constants
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.MapView

@Suppress("TooManyFunctions") // Suppress issue since interface has more than 12 functions.
internal class OmhMapViewImpl(context: Context) : OmhMapView {

    private var containerView: View
    private var mapView: MapView
    private var centerMyLocationButton: ImageButton
    private var centerLocation: (() -> Unit)? = null

    init {
        Configuration.getInstance()
            .load(context, PreferenceManager.getDefaultSharedPreferences(context))

        containerView = LayoutInflater.from(context).inflate(R.layout.map_view, null)
        mapView = containerView.findViewById<MapView>(R.id.omh_osmdroid_map_view).apply {
            minZoomLevel = Constants.MIN_ZOOM_LEVEL
            maxZoomLevel = Constants.MAX_ZOOM_LEVEL
            isHorizontalMapRepetitionEnabled = false
            isVerticalMapRepetitionEnabled = false
            limitScrollArea()
            postInvalidate()
        }
        centerMyLocationButton =
            containerView.findViewById(R.id.omh_osmdroid_map_view_button_center_mylocation)

        centerMyLocationButton.visibility = View.GONE
    }

    fun setOnCenterLocationButtonClickListener(onClick: OnClickListener) {
        centerMyLocationButton.setOnClickListener {
            onClick.onClick(centerMyLocationButton)
            centerLocation?.invoke()
        }
    }

    fun setCenterLocation(centerLocation: () -> Unit) {
        this.centerLocation = centerLocation
    }

    fun setCenterLocationButtonEnabled(isEnabled: Boolean) {
        centerMyLocationButton.visibility = if (isEnabled) View.VISIBLE else View.GONE
    }

    private fun MapView.limitScrollArea() {
        val tileSystem = MapView.getTileSystem()
        setScrollableAreaLimitDouble(
            BoundingBox(
                tileSystem.maxLatitude,
                tileSystem.maxLongitude,
                tileSystem.minLatitude,
                tileSystem.minLongitude
            )
        )
    }

    override fun getView(): View {
        return containerView
    }

    override fun getMapAsync(omhOnMapReadyCallback: OmhOnMapReadyCallback) {
        mapView.let { secureMapView ->
            val omhMapView = OmhMapImpl(secureMapView, this)
            omhOnMapReadyCallback.onMapReady(omhMapView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
    }

    override fun onDestroy() {
        // osmdroid doesn't implement this method
    }

    override fun onLowMemory() {
        // osmdroid doesn't implement this method
    }

    override fun onPause() {
        mapView.onPause()
    }

    override fun onResume() {
        mapView.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // osmdroid doesn't implement this method
    }

    override fun onStart() {
        // osmdroid doesn't implement this method
    }

    override fun onStop() {
        // osmdroid doesn't implement this method
    }

    internal class Builder : OmhMapView.Builder {

        override fun build(context: Context): OmhMapView {
            return OmhMapViewImpl(context)
        }
    }
}
