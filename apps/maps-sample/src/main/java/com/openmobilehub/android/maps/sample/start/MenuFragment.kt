package com.openmobilehub.android.maps.sample.start

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.openmobilehub.android.maps.core.factories.OmhMapProvider
import com.openmobilehub.android.maps.sample.BuildConfig
import com.openmobilehub.android.maps.sample.R

data class MapProviderData(val name: String, val path: String)

val googlemapsPath =
    "com.openmobilehub.android.maps.plugin.googlemaps.presentation.OmhMapFactoryImpl"
val openstreetmapPath =
    "com.openmobilehub.android.maps.plugin.openstreetmap.presentation.OmhMapFactoryImpl"
val mapboxPath = "com.openmobilehub.android.maps.plugin.mapbox.presentation.OmhMapFactoryImpl"

data class MyListItem(val title: String, val description: String, val resID: Int)

class ListViewAdapter(context: Context, private val dataSource: List<MyListItem>) :
    ArrayAdapter<MyListItem>(context, 0, dataSource) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.menu_list_item, parent, false)

        val item = dataSource[position]
        itemView.findViewById<TextView>(R.id.titleTextView).text = item.title
        itemView.findViewById<TextView>(R.id.descriptionTextView).text = item.description

        return itemView
    }
}

class MenuFragment : Fragment() {

    private var mapProviderSpinner: Spinner? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            MyListItem(
                "Marker Map",
                "Map showcasing the markers",
                R.id.action_menuFragment_to_mapFragment
            ),
            MyListItem(
                "Polyline Map",
                "Map showcasing the polylines",
                R.id.action_menuFragment_to_mapPolylinesFragment
            ),
            MyListItem(
                "Polygon Map",
                "Map showcasing the polygons",
                R.id.action_menuFragment_to_mapPolygonsFragment
            ),
            MyListItem(
                "Custom Styles Map",
                "Map showcasing custom styles",
                R.id.action_menuFragment_to_mapStylesFragment
            ),
        )

        val adapter = ListViewAdapter(requireContext(), items)
        val listView: ListView = view.findViewById(R.id.listView)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            findNavController().navigate(items[position].resID)
        }

        mapProviderSpinner = view.findViewById(R.id.spinner_mapProvider)

        // Define the options for the spinner
        var mapProviders = arrayOf(
            MapProviderData("OpenStreetMap", openstreetmapPath),
            MapProviderData("Mapbox", mapboxPath)
        )

        if (isGooglePlayServicesAvailable(requireContext())) {
            mapProviders = mapProviders.plus(MapProviderData("Google", googlemapsPath))
        }

        val adapterMapProvider =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                mapProviders.map { it.name })

        mapProviderSpinner?.adapter = adapterMapProvider

        mapProviderSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                // Another interface callback
            }
        }
    }

    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        return resultCode == ConnectionResult.SUCCESS
    }
}
