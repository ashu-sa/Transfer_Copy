package com.appcake.transfercopy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appcake.transfercopy.R
import com.appcake.transfercopy.data.Contacts

class ContactAdapter(var list:ArrayList<Contacts>): RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {
    class ContactViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var contactName = itemView.findViewById<TextView>(R.id.contact_text)
    }
    fun setFilteredList(list:ArrayList<Contacts>){
        this.list= list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.each_contacts_layout,parent,false)
        return ContactViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        var currentPosition = list[position]
        holder.contactName.text = currentPosition.name


    }
}