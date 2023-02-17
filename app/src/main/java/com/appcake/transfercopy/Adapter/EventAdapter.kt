package com.appcake.transfercopy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appcake.transfercopy.R
import com.appcake.transfercopy.data.GoogleCalendar

class EventAdapter (private val list: ArrayList<GoogleCalendar>):
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textview = itemView.findViewById<TextView>(R.id.event_date_text)
        val textview2 = itemView.findViewById<TextView>(R.id.event_time_text)
        val textview3 = itemView.findViewById<TextView>(R.id.event_title_text)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_events_layout,parent,false)
        return EventViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentPosition = list[position]
        holder.textview.text = currentPosition.dtstart
        holder.textview2.text = currentPosition.dtstart
        holder.textview3.text = currentPosition.title
    }
}