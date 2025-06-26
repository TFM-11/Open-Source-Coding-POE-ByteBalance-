package com.example.bytebalanceapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class BudgetGoalAdapter(private val goalList: List<BudgetGoal>) :
    RecyclerView.Adapter<BudgetGoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMonth: TextView = itemView.findViewById(R.id.tvMonth)
        val tvMinGoal: TextView = itemView.findViewById(R.id.tvMinGoal)
        val tvMaxGoal: TextView = itemView.findViewById(R.id.tvMaxGoal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goalList[position]
        holder.tvMonth.text = goal.month
        holder.tvMinGoal.text = "Min: R${goal.minGoal}"
        holder.tvMaxGoal.text = "Max: R${goal.maxGoal}"
    }

    override fun getItemCount(): Int = goalList.size
}
