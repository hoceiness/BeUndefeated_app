package com.example.beundefeatedapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.logo).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.btn_profile).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("SHOW_FRAGMENT", "PROFILE")
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.btn_deposit).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("SHOW_FRAGMENT", "DEPOSIT")
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.btn_withdrawal).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("SHOW_FRAGMENT", "WITHDRAWAL")
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.btn_create_squad).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("SHOW_FRAGMENT", "SQUAD")
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.btn_verification_gcu).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("SHOW_FRAGMENT", "GCU")
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.btn_about_us).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("SHOW_FRAGMENT", "ABOUT")
            intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.logout_button).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }
}
