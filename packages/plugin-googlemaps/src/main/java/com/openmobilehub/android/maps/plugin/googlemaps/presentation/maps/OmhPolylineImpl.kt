import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.SquareCap
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhCap
import com.openmobilehub.android.maps.core.presentation.models.OmhDash
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhMarker
import com.openmobilehub.android.maps.core.presentation.interfaces.maps.OmhPolyline
import com.openmobilehub.android.maps.core.presentation.models.OmhButtCap
import com.openmobilehub.android.maps.core.presentation.models.OmhCoordinate
import com.openmobilehub.android.maps.core.presentation.models.OmhDot
import com.openmobilehub.android.maps.core.presentation.models.OmhGap
import com.openmobilehub.android.maps.core.presentation.models.OmhPatternItem
import com.openmobilehub.android.maps.core.presentation.models.OmhRoundCap
import com.openmobilehub.android.maps.core.presentation.models.OmhSquareCap
import com.openmobilehub.android.maps.plugin.googlemaps.utils.ConverterUtils

internal class OmhPolylineImpl(private val polyline: Polyline) : OmhPolyline {

    override fun getPoints(): Array<OmhCoordinate> {
        return polyline.points.map { ConverterUtils.convertToOmhCoordinate(it) }.toTypedArray()
    }
    //TODO: Replace with omhCoordinates
    override fun setPoints(omhCoordinate: Array<OmhCoordinate>) {
        polyline.points = omhCoordinate.map { ConverterUtils.convertToLatLng(it) }
    }

    override fun getColor(): Int {
        return polyline.color
    }

    override fun setWidth(width: Float) {
        polyline.width = width
    }

    override fun setVisible(visible: Boolean) {
        polyline.isVisible = visible
    }

    override fun setZIndex(zIndex: Float) {
        polyline.zIndex = zIndex
    }

    override fun setColor(color: Int) {
//        polyline.color = color
    }

    override fun getWidth(): Float {
        return polyline.width
    }

    override fun isVisible(): Boolean {
        return polyline.isVisible
    }

    override fun getZIndex(): Float {
        return polyline.zIndex
    }

    override fun getJointType(): Int {
        return polyline.jointType
    }

    override fun setJointType(jointType: Int) {
        polyline.jointType = jointType
    }


    override fun setPattern(pattern: List<OmhPatternItem>?) {
        polyline.pattern = pattern?.map { patternItem ->
            when (patternItem) {
                is OmhDot -> Dot()
                is OmhDash -> Dash(patternItem.length)
                is OmhGap -> Gap(patternItem.length)
                else -> throw IllegalArgumentException("Unknown pattern item type")
            }
        }
    }

    override fun getPattern(): List<OmhPatternItem> {
        return polyline.pattern!!.map {
            when (it) {
                is Dot -> OmhDot()
                is Dash -> OmhDash(it.length)
                is Gap -> OmhGap(it.length)
                else -> throw IllegalArgumentException("Unknown pattern item type")
            }
        }
    }

    override fun getStartCap(): OmhCap? {
        return polyline.startCap.let {
            when (it) {
                is RoundCap -> OmhRoundCap()
                is SquareCap -> OmhSquareCap()
                is ButtCap -> OmhButtCap()
                else -> throw IllegalArgumentException("Unknown cap type")
            }
        }
    }

    override fun setStartCap(startCap: OmhCap?) {
        polyline.startCap = when (startCap) {
            is OmhRoundCap -> RoundCap()
            is OmhSquareCap -> SquareCap()
            is OmhButtCap -> ButtCap()
            else -> throw IllegalArgumentException("Unknown cap type")
        }
    }

    override fun getEndCap(): OmhCap? {
        return polyline.endCap.let {
            when (it) {
                is RoundCap -> OmhRoundCap()
                is SquareCap -> OmhSquareCap()
                is ButtCap -> OmhButtCap()
                else -> throw IllegalArgumentException("Unknown cap type")
            }
        }
    }

    override fun setEndCap(endCap: OmhCap?) {
        polyline.endCap = when (endCap) {
            is OmhRoundCap -> RoundCap()
            is OmhSquareCap -> SquareCap()
            is OmhButtCap -> ButtCap()
            else -> throw IllegalArgumentException("Unknown cap type")
        }
    }
}