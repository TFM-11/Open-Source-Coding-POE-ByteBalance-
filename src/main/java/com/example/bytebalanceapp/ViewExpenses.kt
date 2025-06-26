package com.example.bytebalanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.app.DatePickerDialog
import java.util.*

class ViewExpenses : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemExpenseAdapter: ItemExpense
    private lateinit var database: Database
    private lateinit var backButton: ImageButton
    private lateinit var filterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)

        database = Database(this)

        recyclerView = findViewById(R.id.rvExpenses)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val expenses = database.getAllExpenses().toMutableList()

        // Pass delete callback to adapter
        itemExpenseAdapter = ItemExpense(expenses) { expense, position ->
            try {
                val deleted = database.deleteExpense(expense.id)
                if (deleted) {
                    Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show()
                    itemExpenseAdapter.removeExpenseAt(position)
                } else {
                    Toast.makeText(this, "Error deleting expense", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Unexpected error deleting expense", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.adapter = itemExpenseAdapter

        backButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            val intent = Intent(this, DashBoard::class.java)
            startActivity(intent)
            finish()
        }

        filterButton = findViewById(R.id.btnSelectDateRange)
        filterButton.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, startYear, startMonth, startDay ->
            val startDate = "${startYear}-${String.format("%02d", startMonth + 1)}-${String.format("%02d", startDay)}"

            DatePickerDialog(this, { _, endYear, endMonth, endDay ->
                val endDate = "${endYear}-${String.format("%02d", endMonth + 1)}-${String.format("%02d", endDay)}"

                val filteredExpenses = database.getExpensesByDateRange(startDate, endDate)
                itemExpenseAdapter.updateExpenses(filteredExpenses)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
}
