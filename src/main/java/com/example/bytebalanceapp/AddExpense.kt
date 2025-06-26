package com.example.bytebalanceapp

import android.app.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AddExpense : AppCompatActivity() {
    private lateinit var etAmount: EditText
    private lateinit var btnBack: ImageButton
    private lateinit var btnSelectDate: Button
    private lateinit var btnSelectStartTime: Button
    private lateinit var btnSelectEndTime: Button
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSelectPhoto: Button
    private lateinit var btnSaveExpense: Button
    private lateinit var ivReceiptPhoto: ImageView

    private var selectedImageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 101
    private val calendar = Calendar.getInstance()
    private var selectedDate: String = ""
    private var startTime: String = ""
    private var endTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Bind UI elements
        etAmount = findViewById(R.id.etAmount)
        btnBack = findViewById(R.id.btnBack)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime)
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime)
        etDescription = findViewById(R.id.etDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        btnSaveExpense = findViewById(R.id.btnSaveExpense)
        ivReceiptPhoto = findViewById(R.id.ivReceiptPhoto)

        btnBack.setOnClickListener {
            startActivity(Intent(this, DashBoard::class.java))
            finish()
        }

        val categories = listOf("Food", "Transport", "Entertainment", "Utilities", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        btnSelectDate.setOnClickListener {
            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = format.format(calendar.time)
                btnSelectDate.text = selectedDate
            },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        btnSelectStartTime.setOnClickListener {
            val timePicker = TimePickerDialog(this, { _, hour, minute ->
                startTime = String.format("%02d:%02d", hour, minute)
                btnSelectStartTime.text = startTime
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePicker.show()
        }

        btnSelectEndTime.setOnClickListener {
            val timePicker = TimePickerDialog(this, { _, hour, minute ->
                endTime = String.format("%02d:%02d", hour, minute)
                btnSelectEndTime.text = endTime
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePicker.show()
        }

        btnSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun saveExpense() {
        val description = etDescription.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val imageUri = selectedImageUri?.toString()
        val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show()
            return
        }

        val db = Database(this)
        val success = db.insertExpense(description, category, imageUri, selectedDate, startTime, endTime, amount)

        if (success) {
            Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            ivReceiptPhoto.setImageURI(selectedImageUri)
        }
    }
}

