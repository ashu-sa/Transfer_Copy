package com.appcake.transfercopy.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.appcake.transfercopy.R
import com.bumptech.glide.Glide

class PhotoAdapter(var list:ArrayList<String>):Adapter<PhotoAdapter.PhotoViewHolder>() {
    class PhotoViewHolder(itemView: View):ViewHolder(itemView) {
      val photoView = itemView.findViewById<ImageView>(R.id.photo_view)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.each_photo_background,parent,false)
        return PhotoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        var currentPosition = list[position]
        Glide.with(holder.itemView.context).load(list.get(position)).into(holder.photoView)
        holder.itemView.setOnClickListener {
            val overlayDrawable = ContextCompat.getDrawable(holder.itemView.context, R.drawable.selected_item_bg)!!
            holder.photoView.overlay.add(overlayDrawable)
        }
    }
}