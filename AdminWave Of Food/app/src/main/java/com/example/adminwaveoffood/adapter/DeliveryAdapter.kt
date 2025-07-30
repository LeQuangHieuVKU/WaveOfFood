package com.example.adminwaveoffood.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminwaveoffood.databinding.DeliveryItemBinding

class DeliveryAdapter (private val customerNames:MutableList<String>, private val moneyStatus:MutableList<Boolean>):RecyclerView.Adapter<DeliveryAdapter.DelivelyViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DelivelyViewHolder {
        val binding = DeliveryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DelivelyViewHolder(binding)
    }



    override fun onBindViewHolder(holder: DelivelyViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size


    inner class DelivelyViewHolder(private val binding: DeliveryItemBinding) :RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                customerName.text = customerNames[position]
                if (moneyStatus[position] == true){
                    statusMoney.text = "Received"
                }else{
                    statusMoney.text = "Not Received"
                }
                val colorMap = mapOf(false to Color.RED,true to Color.GREEN)
                statusMoney.setTextColor(colorMap[moneyStatus[position]]?:Color.BLACK)
                statusColor.backgroundTintList = ColorStateList.valueOf(colorMap[moneyStatus[position]]?:Color.BLACK)
            }
        }

    }
}