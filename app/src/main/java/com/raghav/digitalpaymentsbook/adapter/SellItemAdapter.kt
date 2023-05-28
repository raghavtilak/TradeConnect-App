package com.raghav.digitalpaymentsbook.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raghav.digitalpaymentsbook.R
import com.raghav.digitalpaymentsbook.data.model.SellItem
import com.raghav.digitalpaymentsbook.databinding.ItemSellBinding
import com.raghav.digitalpaymentsbook.databinding.ItemStoreBinding
import java.text.SimpleDateFormat

class SellItemAdapter (private val onItemClickListener: (retailer: SellItem)->Unit) : ListAdapter<SellItem, SellItemAdapter.ViewHolder>(COMPARATOR) {

    class ViewHolder(val binding: ItemSellBinding) : RecyclerView.ViewHolder(binding.root)

    companion object COMPARATOR : DiffUtil.ItemCallback<SellItem>() {
        override fun areItemsTheSame(oldItem: SellItem, newItem: SellItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SellItem, newItem: SellItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            val c = currentList[holder.adapterPosition]

            if(c.isCustomerSale && c.toRetailer!=null){
                retailerName.text = c.toRetailer.name
                businessName.text = c.toRetailer.businessName
                price.text = "Price: ₹${c.totalPrice}"
                avatarText.text = if (c.toRetailer.businessName.contains(" "))
                    "${c.toRetailer.businessName.split(" ")[0][0]}${c.toRetailer.businessName.split(" ")[1][0]}"
                else
                    c.toRetailer.businessName[0].toString()
                batchCount.text = "Batch(s): ${c.batches.size}"

            }else{

            }


            date.text = SimpleDateFormat("dd/MM/yyyy").format(c.date)

            if(c.due ==0){
                status.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
                status.text = "Paid"
            }else{
                status.backgroundTintList = ColorStateList.valueOf(Color.RED)
                status.text = "Due: ${c.due}"
            }

            root.setOnClickListener {
                onItemClickListener(c)
            }
        }
    }
}


