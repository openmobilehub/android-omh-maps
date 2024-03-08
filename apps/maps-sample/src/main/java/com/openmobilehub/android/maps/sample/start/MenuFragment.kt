package com.openmobilehub.android.maps.sample.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.openmobilehub.android.maps.core.factories.OmhMapProvider
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.adapter.MenuListViewAdapter
import com.openmobilehub.android.maps.sample.model.MapProvider
import com.openmobilehub.android.maps.sample.model.MenuListItem
import com.openmobilehub.android.maps.sample.utils.MapProvidersUtils

class MenuFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMapProviderSpinner(view)
        setupListView(view)
    }

    private fun setupMapProviderSpinner(view: View) {
        val mapProviderSpinner: Spinner = view.findViewById(R.id.spinner_mapProvider)

        val mapProviders = MapProvidersUtils.getAvailableMapProviders(requireContext())

        val adapterMapProvider =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                mapProviders.map { it.name })
        mapProviderSpinner.adapter = adapterMapProvider
        mapProviderSpinner.setSelection(2)
        mapProviderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                OmhMapProvider.Initiator()
                    .addNonGmsPath(mapProviders[position].path)
                    .initialize()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Noop
            }
        }
    }

    private fun setupListView(view: View) {
        val listView: ListView = view.findViewById(R.id.listView)

        val items = listOf(
            MenuListItem(
                "Camera Map",
                "Map showcasing the camera features",
                R.id.action_menuFragment_to_mapCameraFragment
            ),
            MenuListItem(
                "Location Sharing Map",
                "Map showcasing location sharing via deep links",
                R.id.action_menuFragment_to_mapLocationPickerFragment
            ),
            MenuListItem(
                "Marker Map",
                "Map showcasing the markers",
                R.id.action_menuFragment_to_mapMarkersFragment
            ),
            MenuListItem(
                "Info Windows Map",
                "Map showcasing info windows",
                R.id.action_menuFragment_to_mapInfoWindowsFragment
            ),
            MenuListItem(
                "Polyline Map",
                "Map showcasing the polylines",
                R.id.action_menuFragment_to_mapPolylinesFragment
            ),
            MenuListItem(
                "Polygon Map",
                "Map showcasing the polygons",
                R.id.action_menuFragment_to_mapPolygonsFragment
            ),
            MenuListItem(
                "Custom Styles Map",
                "Map showcasing custom styles",
                R.id.action_menuFragment_to_mapStylesFragment
            ),
        )

        val adapter = MenuListViewAdapter(requireContext(), items)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            findNavController().navigate(items[position].resID)
        }
    }
}
