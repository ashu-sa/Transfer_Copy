package com.appcake.transfercopy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appcake.transfercopy.R
import com.bumptech.glide.Glide

class DocAdapter(val list:ArrayList<String>):RecyclerView.Adapter<DocAdapter.DocViewHolder>() {
    class DocViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var docText = itemView.findViewById<TextView>(R.id.doc_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.each_docs_layout,parent,false)
        return DocViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
        var currentPosition = list[position]
    }
}