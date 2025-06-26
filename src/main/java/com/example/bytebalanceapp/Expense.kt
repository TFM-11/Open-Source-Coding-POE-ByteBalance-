package com.example.bytebalanceapp

data class Expense( val id: Int,
                    val date: String,
                    val startTime: String,
                    val endTime: String,
                    val description: String,
                    val category: String,
                    val photoPath: String?,
                    val totalAmount: Double
                    )
