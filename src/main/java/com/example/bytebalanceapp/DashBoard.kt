package com.example.bytebalanceapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashBoard : AppCompatActivity() {

    private lateinit var tvFunctionMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dash_board)

        val btnAddExpense = findViewById<Button>(R.id.btnAddExpense)
        val btnViewExpenses = findViewById<Button>(R.id.btnViewExpenses)
        val btnSetGoals = findViewById<Button>(R.id.btnSetGoals)
        val btnSummary = findViewById<Button>(R.id.btnSummary)
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)

        val logo = findViewById<ImageView>(R.id.imgLogo)
        tvFunctionMessage = findViewById(R.id.tvFunctionMessage)

        // Navigation
        btnLogout.setOnClickListener {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnAddExpense.setOnClickListener {
            showTemporaryMessage("You are now adding a new expense.")
            startActivity(Intent(this, AddExpense::class.java))
        }

        btnViewExpenses.setOnClickListener {
            showTemporaryMessage("Viewing all your saved expenses.")
            startActivity(Intent(this, ViewExpenses::class.java))
        }


        btnSetGoals.setOnClickListener {
            showTemporaryMessage("Set your monthly budgeting goals.")
            startActivity(Intent(this, SetBudgetGoals::class.java))
        }

        btnSummary.setOnClickListener {
            showTemporaryMessage("Viewing summary and analytics.")
            startActivity(Intent(this, ViewSummary::class.java))
        }

        // Logo Animations
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val bounce = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val rotate = AnimationUtils.loadAnimation(this, R.anim.spin)

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                logo.startAnimation(bounce)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        bounce.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                logo.startAnimation(rotate)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        logo.startAnimation(fadeIn)
    }

    private fun showTemporaryMessage(message: String) {
        tvFunctionMessage.text = message
        Handler(Looper.getMainLooper()).postDelayed({
            tvFunctionMessage.text = ""
        }, 3000)
    }
}
