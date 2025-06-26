package com.example.bytebalanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddCategory : AppCompatActivity() {
    private lateinit var etCategoryName: EditText
    private lateinit var btnSaveCategory: Button
    private lateinit var btnBack: ImageButton
    private lateinit var db: Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        etCategoryName = findViewById(R.id.etCategoryName)
        btnSaveCategory = findViewById(R.id.btnSaveCategory)
        btnBack = findViewById(R.id.btnBack)
        db = Database(this)

        // Back button handler
        btnBack.setOnClickListener {
            startActivity(Intent(this, DashBoard::class.java))
            finish()
        }


        // Save button handler
        btnSaveCategory.setOnClickListener {
            val categoryName = etCategoryName.text.toString().trim()

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            } else {
                val success = db.insertCategory(categoryName)
                if (success) {
                    Toast.makeText(this, "Category saved successfully", Toast.LENGTH_SHORT).show()
                    etCategoryName.text.clear()
                } else {
                    Toast.makeText(this, "Category already exists or failed to save", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
