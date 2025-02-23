package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nubullstocksv2.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnTogglePassword.setOnClickListener {
            togglePasswordVisibility(binding.etPassword, binding.btnTogglePassword)
        }

        binding.btnToggleVerifyPassword.setOnClickListener {
            togglePasswordVisibility(binding.etVerifyPassword, binding.btnToggleVerifyPassword)
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val verifyPassword = binding.etVerifyPassword.text.toString().trim()
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val middleName = binding.etMiddleName.text.toString().trim()
            val contactNumber = binding.etContactNumber.text.toString().trim()
            val studentNumber = binding.etStudentNumber.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || verifyPassword.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || contactNumber.isEmpty() || studentNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != verifyPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            saveUserData(userId, email, firstName, lastName, middleName, contactNumber, studentNumber)
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Registration Failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        binding.tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
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

    private fun saveUserData(userId: String, email: String, firstName: String, lastName: String, middleName: String, contactNumber: String, studentNumber: String) {
        val database = FirebaseDatabase.getInstance().getReference("users").child(userId)
        val userMap = mapOf(
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "middleName" to middleName,
            "contactNumber" to contactNumber,
            "studentNumber" to studentNumber,
            "role" to "customer" // Default role
        )

        database.setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
