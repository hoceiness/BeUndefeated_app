package com.example.beundefeatedapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val googleSignInButton = findViewById<Button>(R.id.google_sign_in_button)
        googleSignInButton.setOnClickListener {
            Toast.makeText(this, "Signing in with Google...", Toast.LENGTH_SHORT).show()
        }

        val dontHaveAccount = findViewById<TextView>(R.id.dont_have_account)
        dontHaveAccount.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<Button>(R.id.login_button)
        val emailEditText = findViewById<EditText>(R.id.email_edit_text)
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("EMAIL", email)
                // Clear the back stack so pressing back doesn't exit the app immediately after login
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
