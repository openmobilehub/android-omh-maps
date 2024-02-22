package com.openmobilehub.android.maps.sample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.openmobilehub.android.maps.sample.R
import com.openmobilehub.android.maps.sample.model.MenuListItem

class MenuListViewAdapter(context: Context, private val dataSource: List<MenuListItem>) :
    ArrayAdapter<MenuListItem>(context, 0, dataSource) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.menu_list_item, parent, false)

        val item = dataSource[position]
        itemView.findViewById<TextView>(R.id.titleTextView).text = item.title
        itemView.findViewById<TextView>(R.id.descriptionTextView).text = item.description

        return itemView
    }
}
