package com.example.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.databinding.NotificationItemBinding

class NotificationAdapter(private var notification: ArrayList<String>,private var notificationImage: ArrayList<Int>):RecyclerView.Adapter<NotificationAdapter.NotificactionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificactionViewHolder {
        val binding = NotificationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificactionViewHolder(binding)
    }

    override fun getItemCount(): Int = notification.size

    override fun onBindViewHolder(holder: NotificactionViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class NotificactionViewHolder(private var binding: NotificationItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                notificationTextView.text = notification[position]
                notificationImageView.setImageResource(notificationImage[position])
            }

        }

    }
}