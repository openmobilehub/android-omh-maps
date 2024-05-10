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
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.azure.android.maps.control.MapMath
import com.azure.android.maps.control.Popup
import com.azure.android.maps.control.data.Pixel
import com.azure.android.maps.control.layer.SymbolLayer
import com.azure.android.maps.control.options.AnchorType
import com.azure.android.maps.control.options.Option
import com.azure.android.maps.control.options.PopupOptions
import com.openmobilehub.android.maps.core.R
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhInfoWindowViewFactory
import com.openmobilehub.android.maps.core.utils.ScreenUnitConverter
import com.openmobilehub.android.maps.core.utils.cartesian.Offset2D
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.IMapViewDelegate
import com.openmobilehub.android.maps.plugin.azuremaps.presentation.interfaces.ITouchInteractable
import com.openmobilehub.android.maps.plugin.azuremaps.utils.CoordinateConverter

@SuppressWarnings("TooManyFunctions", "LongParameterList")
internal class OmhInfoWindow(
    private var title: String?,
    private var snippet: String?,
    private var infoWindowAnchor: Pair<Float, Float>,
    private val mapViewDelegate: IMapViewDelegate,
    internal val omhMarker: OmhMarkerImpl,
    private val popup: Popup,
) : ITouchInteractable {
    private var infoWindowIsVisible: Boolean = false

    private var customInfoWindowViewFactory: OmhInfoWindowViewFactory? = null
    private var infoWindowContentsViewFactory: OmhInfoWindowViewFactory? = null

    private var windowViewWidth: Int = 0
    private var windowViewHeight: Int = 0

    // stored the view ref since it is needed for closing the IW, which by default triggers an a11y event on it
    internal var windowView: View? = null

    init {
        popup.setOptions(
            PopupOptions.anchor(AnchorType.CENTER),

            // hide the close button
            PopupOptions.closeButton(false),
            Option(PopupOptions.VISIBLE, false)
        )
        popup.close(View(mapViewDelegate.getContext()))
    }

    fun getIsInfoWindowShown(): Boolean {
        return infoWindowIsVisible
    }

    fun setInfoWindowVisibility(visible: Boolean) {
        val wasInfoWindowShown = getIsInfoWindowShown()

        infoWindowIsVisible = visible

        if (wasInfoWindowShown != visible) {
            // visibility changed
            mapViewDelegate.onInfoWindowOpenStatusChange(omhMarker, visible)
            invalidateInfoWindow()

            if (visible) {
                popup.open()
            } else {
                popup.close(windowView)
                windowView = null // clear the view ref now that it's closed
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
            -omhMarker.iconWidth / 4.0 + omhMarker.iconWidth / 8.0,
            // since the default (reference) anchor for the IW icon is being bottom-edge-to-marker-icon's-center,
            // we want to offset it up by half the marker icon's height
            -omhMarker.iconHeight.toDouble() / 2.0
        )
    }

    internal fun updatePosition() {
        val markerPoint = CoordinateConverter.convertToPoint(omhMarker.getPosition())

        val screenOffset = omhMarker.getHandleTopOffset() + getHandleCenterOffset()
        popup.setOptions(
            PopupOptions.position(MapMath.getPosition(markerPoint)),
            PopupOptions.pixelOffset(
                Pixel(
                    screenOffset.x.toLong(),
                    screenOffset.y.toLong()
                )
            )
        )
    }

    @SuppressWarnings("MagicNumber")
    fun invalidateInfoWindow() {
        windowView = if (customInfoWindowViewFactory != null) {
            customInfoWindowViewFactory!!.createInfoWindowView(omhMarker)
        } else {
            renderDefaultInfoWindowView(
                infoWindowContentsViewFactory?.createInfoWindowView(
                    omhMarker
                )
            )
        }

        windowView!!.setOnClickListener {
            mapViewDelegate.onInfoWindowClick(omhMarker)
        }

        windowView!!.setOnLongClickListener {
            mapViewDelegate.onInfoWindowLongClick(omhMarker)
        }

        val markerPoint = CoordinateConverter.convertToPoint(omhMarker.getPosition())

        popup.setOptions(
            PopupOptions.content(windowView!!),
            PopupOptions.position(MapMath.getPosition(markerPoint)),
            Option(PopupOptions.VISIBLE, infoWindowIsVisible)
        )

        windowView!!.getViewTreeObserver()
            .addOnGlobalLayoutListener {
                if (windowView !== null) {
                    synchronized(windowView!!) {
                        windowViewWidth = windowView!!.width
                        windowViewHeight = windowView!!.height

                        updatePosition()
                    }
                }
            }

        updatePosition()
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

    override fun getDraggable(): Boolean {
        return false
    }

    @SuppressWarnings("MagicNumber")
    override fun getHandleCenterOffset(): Offset2D<Double> {
        val offset = getInfoWindowIconOffset()
        val anchorOffsetX = (infoWindowAnchor.first - 0.5) * omhMarker.iconWidth / 4.0
        val anchorOffsetY = infoWindowAnchor.second * omhMarker.iconHeight

        return Offset2D(
            offset.x + anchorOffsetX,
            // also, translate up (-) by the IW's height to ensure it always fits above the marker & add a slight margin
            offset.y + anchorOffsetY + omhMarker.iconHeight / 2.0 - windowViewHeight / 2.0 - omhMarker.iconHeight * 0.1
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
        invalidateInfoWindow()
    }

    fun setCustomInfoWindowContentsViewFactory(factory: OmhInfoWindowViewFactory?) {
        infoWindowContentsViewFactory = factory
        invalidateInfoWindow()
    }

    fun remove() {
        mapViewDelegate.removePopup(popup)
    }
}
