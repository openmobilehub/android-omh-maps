package com.openmobilehub.android.maps.sample.model

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class InfoDisplay(private val fragment: Fragment) {
    private val view: View
        get() = fragment.requireView()

    fun showMessage(message: String) {
        val bar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        bar.setAction("Dismiss") {
            bar.dismiss()
        }
        bar.show()
    }

    fun showMessage(message: Int) {
        val bar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
        bar.setAction("Dismiss") {
            bar.dismiss()
        }
        bar.show()
    }
}
