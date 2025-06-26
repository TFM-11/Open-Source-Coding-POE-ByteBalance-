package com.example.bytebalanceapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class ViewSummary : AppCompatActivity() {

    private lateinit var db: Database
    private lateinit var rvSummary: RecyclerView
    private lateinit var tvDateRange: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var btnSelectDateRange: TextView
    private lateinit var btnSelectMonth: Button
    private lateinit var pieChart: PieChart

    private var startDate: String? = null
    private var endDate: String? = null
    private var selectedMonth: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_summary)

        db = Database(this)
        rvSummary = findViewById(R.id.rvSummary)
        tvDateRange = findViewById(R.id.tvDateRange)
        btnBack = findViewById(R.id.btnBack)
        btnSelectDateRange = findViewById(R.id.btnSelectDateRange)
        btnSelectMonth = findViewById(R.id.btnSelectMonth)
        pieChart = findViewById(R.id.pieChart)

        tvDateRange.text = "Showing all time expenses"

        val summaryList = db.getCategoryTotals()
        rvSummary.layoutManager = LinearLayoutManager(this)
        rvSummary.adapter = ViewSummaryAdapter(summaryList)
        updatePieChart(summaryList)

        btnBack.setOnClickListener {
            finish()
        }

        btnSelectDateRange.setOnClickListener {
            showDateRangePicker()
        }

        btnSelectMonth.setOnClickListener {
            showMonthPicker()
        }
    }

    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, startYear, startMonth, startDay ->
            startDate = String.format("%04d-%02d-%02d", startYear, startMonth + 1, startDay)

            DatePickerDialog(this, { _, endYear, endMonth, endDay ->
                endDate = String.format("%04d-%02d-%02d", endYear, endMonth + 1, endDay)
                tvDateRange.text = "Expenses from $startDate to $endDate"
                loadFilteredSummary()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).apply {
                setTitle("Select End Date")
                show()
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).apply {
            setTitle("Select Start Date")
            show()
        }
    }

    private fun showMonthPicker() {
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val selectedMonthIndex = months.indexOf(selectedMonth).takeIf { it >= 0 } ?: 0

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select a Month")
        builder.setSingleChoiceItems(months.toTypedArray(), selectedMonthIndex) { dialogInterface: DialogInterface, which: Int ->
            selectedMonth = months[which]
            tvDateRange.text = "Budget goals for $selectedMonth"
            loadFilteredSummaryForMonth(selectedMonth!!)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun loadFilteredSummary() {
        if (startDate != null && endDate != null) {
            val filteredSummaryList = db.getCategoryTotals(startDate!!, endDate!!)
            rvSummary.adapter = ViewSummaryAdapter(filteredSummaryList)
            updatePieChart(filteredSummaryList)
        }
    }

    private fun loadFilteredSummaryForMonth(month: String) {
        if (month.isNotEmpty()) {
            val budgetGoalList = db.getBudgetGoalsForMonth(month)
            rvSummary.adapter = BudgetGoalAdapter(budgetGoalList)

            // Convert each BudgetGoal into two ItemSummaryData entries
            val itemSummaryList = budgetGoalList.flatMap { goal ->
                listOf(
                    ItemSummaryData(category = "${goal.month} Min Goal", totalAmount = goal.minGoal),
                    ItemSummaryData(category = "${goal.month} Max Goal", totalAmount = goal.maxGoal)
                )
            }

            updatePieChart(itemSummaryList)

            if (budgetGoalList.isEmpty()) {
                Toast.makeText(this, "No budget goals found for $month", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No month selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updatePieChart(dataList: List<ItemSummaryData>) {
        val entries = ArrayList<PieEntry>()
        for (item in dataList) {
            entries.add(PieEntry(item.totalAmount.toFloat(), item.category))
        }

        val dataSet = PieDataSet(entries, "Category Breakdown")
        dataSet.colors = listOf(
            Color.rgb(76, 175, 80),
            Color.rgb(255, 193, 7),
            Color.rgb(244, 67, 54),
            Color.rgb(33, 150, 243),
            Color.rgb(156, 39, 176)
        )

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(14f)
        pieData.setValueTextColor(Color.WHITE)

        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Summary"
        pieChart.setUsePercentValues(true)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}
