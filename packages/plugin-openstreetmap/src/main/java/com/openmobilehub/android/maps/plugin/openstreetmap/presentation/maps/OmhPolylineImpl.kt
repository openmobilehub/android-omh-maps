package com.openmobilehub.android.maps.plugin.openstreetmap.presentation.maps

import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhPatternItem
import com.openmobilehub.android.maps.plugin.openstreetmap.utils.ConverterUtils
import org.osmdroid.views.overlay.Polyline

internal class OmhPolylineImpl(private val polyline: Polyline) : OmhPolyline {
    override fun getPoints(): Array<OmhCoordinate> {
        return polyline.actualPoints.map { ConverterUtils.convertToOmhCoordinate(it) }.toTypedArray()
    }

    //TODO: Replace with omhCoordinates
    override fun setPoints(omhCoordinate: Array<OmhCoordinate>) {
        polyline.setPoints(omhCoordinate.map { ConverterUtils.convertToGeoPoint(it) })
    }

    override fun getColor(): Int {
        return polyline.outlinePaint.color
    }

    override fun setWidth(width: Float) {
        polyline.outlinePaint.strokeWidth = width
    }

    override fun setVisible(visible: Boolean) {
        polyline.isVisible = visible
    }

    override fun setZIndex(zIndex: Float) {
        // Not implemented
    }

    override fun setColor(color: Int) {
        polyline.outlinePaint.color = color
    }

    override fun getWidth(): Float {
        return polyline.outlinePaint.strokeWidth
    }

    override fun isVisible(): Boolean {
        return polyline.isVisible
    }

    override fun getZIndex(): Float {
        // not implemented
        return 0f
    }

    override fun getJointType(): Int {
        TODO("Not yet implemented")
    }

    override fun setJointType(jointType: Int) {
        TODO("Not yet implemented")
    }

    override fun getPattern(): List<OmhPatternItem>? {
        TODO("Not yet implemented")
    }

    override fun setPattern(pattern: List<OmhPatternItem>?) {
        TODO("Not yet implemented")
    }

    override fun getStartCap(): OmhCap? {
        TODO("Not yet implemented")
    }

    override fun setStartCap(startCap: OmhCap?) {
        TODO("Not yet implemented")
    }

    override fun getEndCap(): OmhCap? {
        TODO("Not yet implemented")
    }

    override fun setEndCap(endCap: OmhCap?) {
        TODO("Not yet implemented")
    }


}