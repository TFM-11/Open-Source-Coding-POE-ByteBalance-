package com.example.bytebalanceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ItemExpense(
    private var expenseList: MutableList<Expense>,
    private val onDeleteClick: (Expense, Int) -> Unit
) : RecyclerView.Adapter<ItemExpense.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        val btnDeleteExpense: ImageButton = itemView.findViewById(R.id.btnDeleteExpense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.tvDescription.text = expense.description
        holder.tvCategory.text = "Category: ${expense.category}"
        holder.tvDateTime.text = "${expense.date} | ${expense.startTime} - ${expense.endTime}"

        holder.btnDeleteExpense.setOnClickListener {
            onDeleteClick(expense, position)
        }
    }

    override fun getItemCount(): Int = expenseList.size

    fun updateExpenses(updatedExpenses: List<Expense>) {
        expenseList.clear()
        expenseList.addAll(updatedExpenses)
        notifyDataSetChanged()
    }

    fun removeExpenseAt(position: Int) {
        if (position in expenseList.indices) {
            expenseList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
