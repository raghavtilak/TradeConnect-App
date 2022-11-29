package com.raghav.digitalpaymentsbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raghav.digitalpaymentsbook.data.model.Retailer
import com.raghav.digitalpaymentsbook.databinding.ItemRetailerBinding

class RetailerAdapter(private val onItemClickListener: OnItemClickListener) : ListAdapter<Retailer, RetailerAdapter.ViewHolder>(COMPARATOR) {

    class ViewHolder(val binding: ItemRetailerBinding) : RecyclerView.ViewHolder(binding.root)

    companion object COMPARATOR : DiffUtil.ItemCallback<Retailer>(){
        override fun areItemsTheSame(oldItem: Retailer, newItem: Retailer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Retailer, newItem: Retailer): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRetailerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            val c = currentList[holder.adapterPosition]
            retailerName.text = c.name
            shopName.text = c.shopName
            phone.text = c.phone
            avatarText.text = if(c.shopName.contains(" "))
                "${c.shopName.split(" ")[0][0]}${c.shopName.split(" ")[1][0]}"
            else
                c.shopName[0].toString()

            root.setOnClickListener {
                onItemClickListener.onItemClick(c)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(retailer: Retailer)
    }

}