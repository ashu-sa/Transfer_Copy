package com.appcake.transfercopy.Adapter

import android.net.Uri
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appcake.transfercopy.R
import com.appcake.transfercopy.data.Video
import com.bumptech.glide.Glide
import java.io.File

class VideoAdapter(var list:ArrayList<Video>): RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {
    class VideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val videoView = itemView.findViewById<ImageView>(R.id.video_view)
        var videoDuration = itemView.findViewById<TextView>(R.id.duration_text)
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
        holder.videoDuration.text = DateUtils.formatElapsedTime(currentPosition.duration/1000)
        Glide.with(holder.itemView.context).asBitmap().load(currentPosition.artUri).into(holder.videoView)

    }
}