package com.openmobilehub.android.maps.sample.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import com.openmobilehub.android.maps.sample.R

class PanelSpinner @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var titleTextView: TextView
    var spinner: Spinner

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.panel_spinner, this, true)

        titleTextView = findViewById(R.id.panelSeekbarTitle)
        spinner = findViewById(R.id.panelSpinner)

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

    private fun getResourceStrings(resourceIds: IntArray): MutableList<String> {
        val strings = mutableListOf<String>();
        for (i in resourceIds.indices) {
            strings.add(context.getString(resourceIds[i]))
        }
        return strings
    }

    fun setValues(context: Context, values: IntArray) {
        spinner.adapter = ArrayAdapter(
            context, android.R.layout.simple_spinner_item,
            getResourceStrings(values)
        )
    }

    fun setOnItemSelectedCallback(callback: (position: Int) -> Unit) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                callback(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }
}
