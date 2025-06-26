package com.example.bytebalanceapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var db: Database
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activiy)

        db = Database(this)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnBack = findViewById(R.id.btnBack)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                val isValid = db.checkUser(username, password)
                if (isValid) {
                    // User logged in successfully
                    trackLoginAndReward()

                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashBoard::class.java)
                    intent.putExtra("username", username) // Optional: send username
                    startActivity(intent)
                    finish() // Prevent going back to login with back button
                } else {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun trackLoginAndReward() {
        val prefs = getSharedPreferences("userPrefs", MODE_PRIVATE)

        // Get today's date as String yyyy-MM-dd
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())

        val lastLoginDate = prefs.getString("lastLoginDate", "")
        var loginCount = prefs.getInt("loginCount", 0)
        val badgeAwardedDate = prefs.getString("badgeAwardedDate", "")

        if (lastLoginDate == today) {
            // Same day, increment count
            loginCount++
        } else {
            // New day, reset count
            loginCount = 1
        }

        // Save updated login count and date
        prefs.edit()
            .putString("lastLoginDate", today)
            .putInt("loginCount", loginCount)
            .apply()

        // Check if user logged in twice today and badge not yet awarded today
        if (loginCount >= 2 && badgeAwardedDate != today) {
            showRewardBadge()

            // Save badge awarded date so it doesn't show repeatedly today
            prefs.edit()
                .putString("badgeAwardedDate", today)
                .apply()
        }
    }

    private fun showRewardBadge() {
        Toast.makeText(this, "🎉 Congrats! You've earned a reward badge for logging in twice today!", Toast.LENGTH_LONG).show()

    }
}
