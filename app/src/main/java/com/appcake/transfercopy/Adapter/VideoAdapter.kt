package com.appcake.transfercopy.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appcake.transfercopy.R
import com.bumptech.glide.Glide
import java.io.File

class VideoAdapter(var list:ArrayList<String>): RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    class VideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val videoView = itemView.findViewById<ImageView>(R.id.video_view)
        var videoText = itemView.findViewById<TextView>(R.id.duration_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.each_video_layout,parent,false)
        return VideoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        var currentPosition = list[position]
        Glide.with(holder.itemView.context).load(list.get(position)).into(holder.videoView)
    }
}