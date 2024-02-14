package com.openmobilehub.android.maps.sample.start

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.openmobilehub.android.maps.sample.R

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
    }
}
