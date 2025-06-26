package com.example.bytebalanceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewSummaryAdapter(private val summaryList: List<ItemSummaryData>) :
    RecyclerView.Adapter<ViewSummaryAdapter.SummaryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SummaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_summary_row, parent, false)
        return SummaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SummaryViewHolder, position: Int) {
        val item = summaryList[position]
        holder.tvCategory.text = item.category
        holder.tvTotal.text = "R ${String.format("%.2f", item.totalAmount)}"
    }

    override fun getItemCount(): Int = summaryList.size

    class SummaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
    }
}
