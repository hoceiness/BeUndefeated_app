package com.example.beundefeatedapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class VerifyOtpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verify_otp)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val email = intent.getStringExtra("EMAIL")
        val otpSentTo = findViewById<TextView>(R.id.otp_sent_to)
        otpSentTo.text = "We\'ve sent an OTP code to your email, $email"

        val verifyButton = findViewById<Button>(R.id.verify_button)
        val resendCode = findViewById<TextView>(R.id.resend_code)
        val alreadyHaveAccount = findViewById<TextView>(R.id.already_have_account)

        verifyButton.isEnabled = true

        object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                resendCode.text = "We will resend the code in %d:%02d".format(minutes, seconds)
                resendCode.isEnabled = false
            }

            override fun onFinish() {
                resendCode.text = "Resend code"
                resendCode.isEnabled = true
                resendCode.setTextColor(getColor(R.color.orange))
            }
        }.start()

        resendCode.setOnClickListener {
            if (resendCode.isEnabled) {
                Toast.makeText(this, "OTP Resent to $email", Toast.LENGTH_SHORT).show()
            }
        }

        verifyButton.setOnClickListener {
            val otpBox1 = findViewById<EditText>(R.id.otp_box_1).text.toString()
            val otpBox2 = findViewById<EditText>(R.id.otp_box_2).text.toString()
            val otpBox3 = findViewById<EditText>(R.id.otp_box_3).text.toString()
            val otpBox4 = findViewById<EditText>(R.id.otp_box_4).text.toString()
            val otpBox5 = findViewById<EditText>(R.id.otp_box_5).text.toString()
            val otpBox6 = findViewById<EditText>(R.id.otp_box_6).text.toString()
            val otpBox7 = findViewById<EditText>(R.id.otp_box_7).text.toString()
            val enteredOtp = otpBox1 + otpBox2 + otpBox3 + otpBox4 + otpBox5 + otpBox6 + otpBox7

            if (enteredOtp == "1234567") {
                Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("EMAIL", email)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }

        alreadyHaveAccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
