package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nubullstocksv2.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnTogglePassword.setOnClickListener {
            togglePasswordVisibility(binding.etPassword, binding.btnTogglePassword)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Authenticate user
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            // Check the user's role
                            checkUserRole(userId)
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Login Failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        binding.tvDontHaveAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }
    }

    private fun togglePasswordVisibility(editText: android.widget.EditText, button: ImageButton) {
        if (editText.transformationMethod is PasswordTransformationMethod) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            button.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            button.setImageResource(android.R.drawable.ic_menu_view)
        }
        editText.setSelection(editText.text.length)
    }

    // Check User Role and Navigate Accordingly
    private fun checkUserRole(userId: String) {
        val database = FirebaseDatabase.getInstance().getReference("users").child(userId)

        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val role = snapshot.child("role").value.toString()

                if (role == "admin") {
                    // Navigate to Admin Dashboard
                    val intent = Intent(this, AdminDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else if (role == "customer") {
                    // Navigate to Customer Home Screen
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get user data.", Toast.LENGTH_SHORT).show()
        }
    }
}
