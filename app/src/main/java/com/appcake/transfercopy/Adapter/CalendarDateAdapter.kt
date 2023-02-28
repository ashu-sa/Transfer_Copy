package com.appcake.transfercopy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appcake.transfercopy.R
import com.appcake.transfercopy.data.GoogleCalendar

class CalendarDateAdapter(private val list: ArrayList<String>,private var listener:RecyclerViewClickListener): RecyclerView.Adapter<CalendarDateAdapter.CalendarDateViewHolder>() {
    interface RecyclerViewClickListener {
        fun onClick(position: Int,dateText:TextView)
    }
   inner class CalendarDateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textview = itemView.findViewById<TextView>(R.id.date_text)
       init {
        itemView.setOnClickListener {
            listener?.onClick(adapterPosition,textview)
        }
    }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.date_text_layout,parent,false)
        return CalendarDateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CalendarDateViewHolder, position: Int) {
        holder.textview.text = list.get(position)
    }
}