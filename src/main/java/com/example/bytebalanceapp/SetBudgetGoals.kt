package com.example.bytebalanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SetBudgetGoals : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_budget_goals)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnSaveGoals = findViewById<Button>(R.id.btnSaveGoals)
        val spinnerMonth = findViewById<Spinner>(R.id.spinnerMonth)
        val etMinGoal = findViewById<EditText>(R.id.etMinGoal)
        val etMaxGoal = findViewById<EditText>(R.id.etMaxGoal)

        // Populate Spinner with months
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = adapter

        // Back button logic
        btnBack.setOnClickListener {
            val intent = Intent(this, DashBoard::class.java)
            startActivity(intent)
            finish()
        }

        // Save goals button logic
        btnSaveGoals.setOnClickListener {
            val minGoal = etMinGoal.text.toString().toDoubleOrNull()
            val maxGoal = etMaxGoal.text.toString().toDoubleOrNull()
            val selectedMonth = spinnerMonth.selectedItem.toString()

            if (minGoal == null || maxGoal == null || minGoal <= 0 || maxGoal <= 0) {
                Toast.makeText(this, "Please enter valid goals", Toast.LENGTH_SHORT).show()
            } else if (minGoal > maxGoal) {
                Toast.makeText(
                    this,
                    "Minimum goal cannot be greater than maximum goal",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // Create a BudgetGoal object
                val budgetGoal = BudgetGoal(selectedMonth, minGoal, maxGoal)

                // Save budgetGoal to the database
                val db = Database(this)
                val success = db.insertBudgetGoal(selectedMonth, minGoal, maxGoal)

                if (success) {
                    Toast.makeText(
                        this,
                        "Budget goals saved for $selectedMonth",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, "Failed to save budget goals", Toast.LENGTH_SHORT).show()
                }

                // Navigate back to the Dashboard
                val intent = Intent(this, DashBoard::class.java)
                startActivity(intent)
                finish()
            }
        }


    }
}