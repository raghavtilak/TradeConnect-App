package com.raghav.digitalpaymentsbook.adapter.spinner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raghav.digitalpaymentsbook.data.model.Batch
import com.raghav.digitalpaymentsbook.databinding.ItemRetailerBinding
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener
import com.skydoves.powerspinner.PowerSpinnerInterface
import com.skydoves.powerspinner.PowerSpinnerView

class BatchSpinnerAdapter(
    powerSpinnerView: PowerSpinnerView,
    val spinnerItems: MutableList<Batch>
) : RecyclerView.Adapter<BatchSpinnerAdapter.BatchSpinnerViewHolder>(),
    PowerSpinnerInterface<Batch> {

    private val NO_SELECTED_INDEX: Int = 0
    override var index: Int = powerSpinnerView.selectedIndex
    override val spinnerView: PowerSpinnerView = powerSpinnerView
    override var onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener<Batch>? = null

    class BatchSpinnerViewHolder(val binding:ItemRetailerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: BatchSpinnerViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            notifyItemSelected(position)
        }
    }

    // You must call the `spinnerView.notifyItemSelected` method to let `PowerSpinnerView` know the item is changed.
    override fun notifyItemSelected(index: Int) {
        if (index == NO_SELECTED_INDEX) return
        val oldIndex = this.index
        this.index = index
        this.spinnerView.notifyItemSelected(index, this.spinnerItems[index].batchNo)
        this.onSpinnerItemSelectedListener?.onItemSelected(
            oldIndex = oldIndex,
            oldItem = oldIndex.takeIf { it != NO_SELECTED_INDEX }?.let { spinnerItems[oldIndex] },
            newIndex = index,
            newItem = this.spinnerItems[index]
        )
    }

    override fun getItemCount(): Int {
        return spinnerItems.size
    }

    override fun setItems(itemList: List<Batch>) {
        spinnerItems.clear()
        spinnerItems.addAll(itemList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BatchSpinnerViewHolder {
        val binding = ItemRetailerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BatchSpinnerViewHolder(binding)
    }


}