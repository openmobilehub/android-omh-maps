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

package com.openmobilehub.android.maps.plugin.azuremaps.presentation.maps

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.drawToBitmap
import androidx.core.view.setPadding
import com.azure.android.maps.control.layer.SymbolLayer
import com.azure.android.maps.control.options.AnchorType
import com.azure.android.maps.control.options.SymbolLayerOptions
import com.azure.android.maps.control.source.DataSource
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhInfoWindowViewFactory
import com.openmobilehub.android.maps.core.utils.ScreenUnitConverter
import com.openmobilehub.android.maps.core.utils.cartesian.Offset2D
import com.openmobilehub.android.maps.core.utils.uuid.DefaultUUIDGenerator
import com.openmobilehub.android.maps.core.utils.uuid.UUIDGenerator
import com.openmobilehub.android.maps.plugin.azuremaps.R
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IMapViewDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.ITouchInteractable
import java.util.concurrent.Executors

@SuppressWarnings("TooManyFunctions", "LongParameterList")
internal class OmhInfoWindow(
    private var title: String?,
    private var snippet: String?,
    private var infoWindowAnchor: Pair<Float, Float>,
    private val mapViewDelegate: IMapViewDelegate,
    internal val omhMarker: OmhMarkerImpl,
    private val uuidGenerator: UUIDGenerator = DefaultUUIDGenerator()
) : ITouchInteractable {
    internal var infoWindowSymbolLayer: SymbolLayer
    internal lateinit var dataSource: DataSource

    private var infoWindowIsVisible: Boolean = false

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
        infoWindowSymbolLayer = SymbolLayer(
            getSymbolLayerID(),
            OmhMarkerImpl.getSourceID(omhMarker.markerUUID),
            SymbolLayerOptions.iconSize(1.0f), // icon scale
            SymbolLayerOptions.iconImage(generateInfoWindowIconID()),
            SymbolLayerOptions.iconAnchor(AnchorType.CENTER),
            SymbolLayerOptions.iconAllowOverlap(true),
            SymbolLayerOptions.iconIgnorePlacement(true),
            SymbolLayerOptions.iconOpacity(0f)
        )
    }

    fun getIsInfoWindowShown(): Boolean {
        return infoWindowIsVisible
    }

    fun setInfoWindowVisibility(visible: Boolean) {
        val wasInfoWindowShown = getIsInfoWindowShown()

        infoWindowSymbolLayer.setOptions(
            SymbolLayerOptions.iconOpacity(if (visible) 1f else 0f)
        )

        if (wasInfoWindowShown != visible) {
            // visibility changed
            mapViewDelegate.onInfoWindowOpenStatusChange(omhMarker, visible)
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
        infoWindowAnchor = iwAnchorU to iwAnchorV

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
        return Offset2D(
            0.0,
            // since the default (reference) anchor for the IW icon is being bottom-edge-to-marker-icon's-center,
            // we want to offset it up by half the marker icon's height
            -omhMarker.iconHeight.toDouble() / 2.0
        )
    }

    internal fun updatePosition() {
        // TODO implement this
//        val markerPoint = CoordinateConverter.convertToPoint(omhMarker.getPosition())
//        val screenCoordinate =
//            mapViewDelegate.pixelForCoordinate(markerPoint) + omhMarker.getHandleTopOffset() + getHandleCenterOffset()
//
//        dataSource.setShapes(
//            Feature.fromGeometry(
//                mapViewDelegate.coordinateForPixel(
//                    screenCoordinate
//                )
//            )
//        )
    }

    fun setDataSource(dataSource: DataSource) {
        this.dataSource = dataSource
    }

    internal fun getSymbolLayerID(): String {
        return "${omhMarker.markerUUID}-omh-marker-info-window-layer"
    }

    @SuppressWarnings("MagicNumber")
    private fun invalidateInfoWindow() {
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
                infoWindowSymbolLayer.setOptions(
                    SymbolLayerOptions.iconImage(infoWindowIconID)
                )

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
            mapViewDelegate.removeImage(it)

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

        mapViewDelegate.addImage(
            infoWindowIconID,
            bitmap
        )

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
    @SuppressLint("InflateParams")
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
        val anchorOffsetX = (infoWindowAnchor.first - 0.5) * omhMarker.iconWidth / 2.0
        val anchorOffsetY = infoWindowAnchor.second * omhMarker.iconHeight

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
        // note: for parity with other providers, there is no check for if the marker is clickable
        // since it should not have an impact on the info window's clickability
        return this.omhMarker.getIsVisible() && this.getIsInfoWindowShown()
    }

    fun setCustomInfoWindowViewFactory(factory: OmhInfoWindowViewFactory?) {
        customInfoWindowViewFactory = factory
    }

    fun setCustomInfoWindowContentsViewFactory(factory: OmhInfoWindowViewFactory?) {
        infoWindowContentsViewFactory = factory
    }
}
