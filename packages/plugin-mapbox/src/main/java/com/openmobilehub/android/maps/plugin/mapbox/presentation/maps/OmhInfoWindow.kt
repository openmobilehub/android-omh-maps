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

package com.openmobilehub.android.maps.plugin.mapbox.presentation.maps

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.drawToBitmap
import androidx.core.view.setPadding
import com.mapbox.geojson.Feature
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhInfoWindowViewFactory
import com.openmobilehub.android.maps.core.utils.ScreenUnitConverter
import com.openmobilehub.android.maps.plugin.mapbox.R
import com.openmobilehub.android.maps.plugin.mapbox.extensions.plus
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IMapInfoWindowManagerDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.IOmhInfoWindowMapViewDelegate
import com.openmobilehub.android.maps.plugin.mapbox.presentation.interfaces.ITouchInteractable
import com.openmobilehub.android.maps.plugin.mapbox.utils.CoordinateConverter
import com.openmobilehub.android.maps.plugin.mapbox.utils.cartesian.Offset2D
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.plugin.mapbox.utils.uuid.UUIDGenerator
import java.util.concurrent.Executors

@SuppressWarnings("TooManyFunctions", "LongParameterList")
internal class OmhInfoWindow(
    private var title: String?,
    private var snippet: String?,
    iwAnchor: Pair<Float, Float>,
    private val infoWindowManagerDelegate: IMapInfoWindowManagerDelegate,
    internal val omhMarker: OmhMarkerImpl,
    private val uuidGenerator: UUIDGenerator = DefaultUUIDGenerator(),
    private val mapViewDelegate: IOmhInfoWindowMapViewDelegate
) : ITouchInteractable {
    private lateinit var style: Style
    private lateinit var geoJsonSource: GeoJsonSource
    internal var infoWindowSymbolLayer: SymbolLayer

    private var bufferedInfoWindowIsVisible: Boolean = false
    internal var bufferedInfoWindowAnchor: Pair<Float, Float>

    private var customInfoWindowViewFactory: OmhInfoWindowViewFactory? = null
    private var infoWindowContentsViewFactory: OmhInfoWindowViewFactory? = null

    internal var lastInfoWindowIconID: String? = null
    internal var iwBitmapWidth: Int = 0
    internal var iwBitmapHeight: Int = 0

    /** Executor used for rendering views that won't be mounted in the UI, but rendered to a [Bitmap] */
    internal var backgroundThreadExecutor = Executors.newSingleThreadExecutor()

    /** Handler used to post tasks to the main (UI) thread */
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    init {
        bufferedInfoWindowAnchor = iwAnchor

        infoWindowSymbolLayer =
            symbolLayer(getSymbolLayerID(), getGeoJsonSourceID()) {
                iconSize(1.0) // icon scale
                iconImage(generateInfoWindowIconID())
                iconAnchor(IconAnchor.CENTER)
                iconAllowOverlap(true)
                iconIgnorePlacement(true)
                visibility(Visibility.NONE)
            }
    }

    fun setGeoJsonSource(geoJsonSource: GeoJsonSource) {
        this.geoJsonSource = geoJsonSource
    }

    fun applyBufferedProperties() {
        check(!this::style.isInitialized) { "Buffered properties have already been applied" }

        synchronized(this) {
            setInfoWindowAnchor(
                bufferedInfoWindowAnchor.first,
                bufferedInfoWindowAnchor.second
            )
        }
    }

    private fun isStyleReady(): Boolean {
        return this::style.isInitialized && this.style.isStyleLoaded()
    }

    private fun addSourceAndLayerToStyle() {
        this.style.addLayer(infoWindowSymbolLayer)
        this.style.addSource(geoJsonSource)
    }

    fun onStyleLoaded(safeStyle: Style) {
        check(!isStyleReady()) { "Buffered properties have already been applied" }

        synchronized(this) {
            this.style = safeStyle

            addSourceAndLayerToStyle()
            applyBufferedProperties()
        }
    }

    fun setInfoWindowVisibility(visible: Boolean) {
        val wasInfoWindowShown = getIsInfoWindowShown()
        if (this::style.isInitialized) {
            infoWindowSymbolLayer.visibility(OmhMarkerImpl.getIconsVisibility(visible))
        } else {
            // if the layer was not added to the map yet, buffer the icon value to apply it later
            bufferedInfoWindowIsVisible = visible
        }

        if (wasInfoWindowShown != visible) {
            // visibility changed
            infoWindowManagerDelegate.onInfoWindowOpenStatusChange(omhMarker, visible)
            invalidateInfoWindow()

            if (!visible) {
                // if now invisible, spare the memory from the view's image - after the IW
                // is shown again, it will immediately invalidate itself & re-render
                removeCurrentStyleImage()
            }
        } else if (visible) {
            // visibility did not change, the info window was and is visible
            // for parity with other providers: re-render the window in such case
            invalidateInfoWindow()
        }
    }

    fun getIsInfoWindowShown(): Boolean {
        return if (this::style.isInitialized) {
            infoWindowSymbolLayer.visibility == OmhMarkerImpl.getIconsVisibility(true)
        } else {
            bufferedInfoWindowIsVisible
        }
    }

    fun getTitle(): String? = title

    fun setTitle(title: String?) {
        this.title = title
        invalidateInfoWindow()
    }

    fun getSnippet(): String? = snippet

    fun setSnippet(snippet: String?) {
        this.snippet = snippet
        invalidateInfoWindow()
    }

    fun setInfoWindowAnchor(iwAnchorU: Float, iwAnchorV: Float) {
        bufferedInfoWindowAnchor = iwAnchorU to iwAnchorV

        updatePosition()
    }

    /**
     * Gets the icon offset for the info window symbol layer, using the
     * current value of `iconSize` from the info window's [SymbolLayer].
     *
     * @return The offset for the info window icon in pixels.
     */
    @SuppressWarnings("MagicNumber")
    private fun getInfoWindowIconOffset(): Offset2D<Double> {
        // we want to mitigate the scaling-by-icon-size behaviour
        // described in the docs: https://docs.mapbox.com/android/maps/api/10.2.0/mapbox-maps-android/com.mapbox.maps.plugin.annotation.generated/-point-annotation/icon-offset.html
        val iconSize = if (this::style.isInitialized) {
            infoWindowSymbolLayer.iconSize ?: SymbolLayer.defaultIconSize
        } else {
            null
        } ?: 1.0

        return Offset2D(
            0.0,
            // since the default (reference) anchor for the IW icon is being bottom-edge-to-marker-icon's-center,
            // we want to offset it up by half the marker icon's height
            -omhMarker.iconHeight.toDouble() / (2.0 * iconSize)
        )
    }

    internal fun updatePosition() {
        if (!::geoJsonSource.isInitialized) return

        val markerPoint = CoordinateConverter.convertToPoint(omhMarker.getPosition())
        val screenCoordinate =
            mapViewDelegate.pixelForCoordinate(markerPoint) + omhMarker.getHandleTopOffset() + getHandleCenterOffset()

        Log.d(
            "OmhInfoWindow",
            "updatePosition: screenCoordinate: $screenCoordinate; rot: ${omhMarker.getRotation()}"
        )

        geoJsonSource.feature(
            Feature.fromGeometry(
                mapViewDelegate.coordinateForPixel(
                    screenCoordinate
                )
            )
        )
    }

    internal fun getGeoJsonSourceID(): String {
        return "${omhMarker.markerUUID}-omh-marker-info-window-geojson-source"
    }

    internal fun getSymbolLayerID(): String {
        return "${omhMarker.markerUUID}-omh-marker-info-window-layer"
    }

    @SuppressWarnings("MagicNumber")
    private fun invalidateInfoWindow() {
        if (!this::style.isInitialized) return

        backgroundThreadExecutor.execute {
            // this scope runs on a background thread
            val windowView: View = if (customInfoWindowViewFactory != null) {
                customInfoWindowViewFactory!!.createInfoWindowView(omhMarker)
            } else {
                renderDefaultInfoWindowView(
                    infoWindowContentsViewFactory?.createInfoWindowView(
                        omhMarker
                    )
                )
            }

            // obtain the view's desired dimensions, also limiting to 90% of screen width & height
            windowView.measure(
                View.MeasureSpec.makeMeasureSpec(
                    (0.9 * mapViewDelegate.getMapWidth()).toInt(),
                    View.MeasureSpec.AT_MOST
                ),
                View.MeasureSpec.makeMeasureSpec(
                    (0.9 * mapViewDelegate.getMapHeight()).toInt(),
                    View.MeasureSpec.AT_MOST
                )
            )

            // make sure the view is laid out (otherwise, drawToBitmap will throw an exception)
            windowView.layout(
                windowView.left,
                windowView.top,
                windowView.measuredWidth,
                windowView.measuredHeight
            )

            val bitmap = windowView.drawToBitmap(Bitmap.Config.ARGB_8888)

            mainThreadHandler.post {
                // this scope runs on the UI thread;
                // only the main (UI) thread can access the Mapbox map as per their rules

                val infoWindowIconID = addOrUpdateMarkerIconImage(bitmap)
                infoWindowSymbolLayer.iconImage(infoWindowIconID)

                updatePosition()
            }
        }
    }

    /**
     * Ensures the current icon is removed before changing it so as not to cause a memory leak;
     * also used just to clear the memory when the IW is hidden.
     */
    private fun removeCurrentStyleImage() {
        lastInfoWindowIconID?.let {
            style.removeStyleImage(it)

            lastInfoWindowIconID = null
        }
    }

    /**
     * Adds or updates the info window icon image on the map.
     *
     * @param bitmap the icon [Bitmap] for the info window.
     *
     * @return the ID of the added or updated info window icon image
     */
    internal fun addOrUpdateMarkerIconImage(
        bitmap: Bitmap
    ): String {
        // ensure the other icon is removed before change so as not to cause a memory leak
        removeCurrentStyleImage()

        val infoWindowIconID = generateInfoWindowIconID()
        lastInfoWindowIconID = infoWindowIconID

        iwBitmapWidth = bitmap.width
        iwBitmapHeight = bitmap.height

        val addImageResult = style.addImage(
            infoWindowIconID,
            bitmap
        )

        addImageResult.error?.let { error ->
            throw IllegalStateException("Failed to add image to map: $error")
        }

        return infoWindowIconID
    }

    /**
     * Renders the default info window view.
     *
     * @param customContents if `null`, renders the whole window with default contents
     * or use custom contents view inside the default window otherwise
     *
     * @return The rendered default info window view.
     */
    @SuppressWarnings("MagicNumber")
    private fun renderDefaultInfoWindowView(customContents: View?): View {
        val defaultWindowView = LayoutInflater.from(omhMarker.context).inflate(
            R.layout.default_info_window,
            null
        )
        val windowLinearLayout =
            defaultWindowView.findViewById<LinearLayout>(R.id.default_info_window_linear_layout)

        if (customContents == null) {
            windowLinearLayout.setPadding(
                ScreenUnitConverter.dpToPx(12f, omhMarker.context).toInt()
            )
        }

        val contentsView = if (customContents == null) {
            val defaultWindowContentsView = LayoutInflater.from(omhMarker.context).inflate(
                R.layout.default_info_window_contents,
                null
            )

            val titleTv = defaultWindowContentsView.findViewById<TextView>(R.id.titleTextView)
            val descriptionTv =
                defaultWindowContentsView.findViewById<TextView>(R.id.descriptionTextView)

            titleTv.text = omhMarker.getTitle()

            val snippet = omhMarker.getSnippet()

            if (snippet.isNullOrBlank()) {
                descriptionTv.visibility = View.GONE
                descriptionTv.text = ""
            } else {
                descriptionTv.visibility = View.VISIBLE
                descriptionTv.text = snippet
            }

            defaultWindowContentsView
        } else {
            customContents
        }

        windowLinearLayout.addView(contentsView)

        return windowLinearLayout
    }

    private fun generateInfoWindowIconID(): String {
        // the random UUID suffix below is to cause an invalidation of the map
        // when the info window is re-rendered (invalidated)
        return "${omhMarker.markerUUID}-omh-marker-icon-info-window-${uuidGenerator.generate()}"
    }

    override fun getDraggable(): Boolean {
        return false
    }

    @SuppressWarnings("MagicNumber")
    override fun getHandleCenterOffset(): Offset2D<Double> {
        val offset = getInfoWindowIconOffset()
        val anchorOffsetX = (bufferedInfoWindowAnchor.first - 0.5) * omhMarker.iconWidth / 2.0
        val anchorOffsetY = bufferedInfoWindowAnchor.second * omhMarker.iconHeight

        return Offset2D(
            offset.x + anchorOffsetX,
            // also, translate up (-) by the IW's height to ensure it always fits above the marker
            offset.y + anchorOffsetY + omhMarker.iconHeight / 2.0 - iwBitmapHeight / 2.0
        )
    }

    override fun getLongClickable(): Boolean {
        return this.getIsInfoWindowShown()
    }

    override fun getClickable(): Boolean {
        return this.omhMarker.getClickable() && this.getIsInfoWindowShown()
    }

    fun setCustomInfoWindowViewFactory(factory: OmhInfoWindowViewFactory?) {
        customInfoWindowViewFactory = factory
    }

    fun setCustomInfoWindowContentsViewFactory(factory: OmhInfoWindowViewFactory?) {
        infoWindowContentsViewFactory = factory
    }
}
