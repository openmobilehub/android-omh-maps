package com.openmobilehub.android.maps.sample.customviews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.openmobilehub.android.maps.sample.R

class PanelColorSeekbar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var titleTextView: TextView
    var customSeekBar: SeekBar

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.panel_color_seekbar, this, true)

        titleTextView = findViewById(R.id.panelSeekbarTitle)
        customSeekBar = findViewById(R.id.panelSeekbar)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PanelSeekbar,
            0, 0
        ).apply {
            try {
                val title = getString(R.styleable.PanelSeekbar_titleText)
                title?.let { titleTextView.text = it }
            } finally {
                recycle()
            }
        }
    }

    private fun seekbarProgressToColor(progress: Int): Int {
        val hsvColor = floatArrayOf(progress.toFloat(), 1f, 1f)
        return Color.HSVToColor(hsvColor)
    }

    fun setOnColorChangedCallback(callback: (color: Int) -> Unit) {
        customSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                callback(seekbarProgressToColor(progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })
    }
}
