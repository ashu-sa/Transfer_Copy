package com.appcake.transfercopy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.appcake.transfercopy.R

class CalenderAdapter(private val list: ArrayList<Int>):Adapter<CalenderAdapter.CalnderViewHolder>() {
    class CalnderViewHolder(itemView: View):ViewHolder(itemView) {
        val textview = itemView.findViewById<TextView>(R.id.date_text)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalnderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.date_text_layout,parent,false)
        return CalnderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CalnderViewHolder, position: Int) {
        val currentPosition = list[position]
        holder.textview.text = list.get(position).toString()
    }
}