package com.raghav.digitalpaymentsbook.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raghav.digitalpaymentsbook.data.model.Product
import com.raghav.digitalpaymentsbook.databinding.ItemCustomerBinding
import com.raghav.digitalpaymentsbook.databinding.ItemProductBinding

class ProductAdapter(private val onRemoveClickListener: OnRemoveClickListener) : ListAdapter<Product, ProductAdapter.ViewHolder>(COMPARATOR) {

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    companion object COMPARATOR : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.apply {
            val p = currentList[holder.adapterPosition]
            productName.text = p.name
            price.text = p.price.toString()
            paid.text = p.price.toString()
            removeBtn.setOnClickListener {
                onRemoveClickListener.onItemRemove(p)
            }
        }
    }

    interface OnRemoveClickListener{
        fun onItemRemove(product: Product)
    }

}