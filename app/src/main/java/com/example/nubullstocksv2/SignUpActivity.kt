package com.example.nubullstocksv2

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nubullstocksv2.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference

        binding.btnSignUp.setOnClickListener {
            val studentNumber = binding.etStudentNumber.text.toString().trim()
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val middleName = binding.etMiddleName.text.toString().trim()
            val contactNumber = binding.etContactNumber.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val verifyPassword = binding.etVerifyPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || verifyPassword.isEmpty() || studentNumber.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || contactNumber.isEmpty()) {
                showToast("All fields are required")
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Invalid Email")
                return@setOnClickListener
            }

            if (password != verifyPassword) {
                showToast("Passwords do not match")
                return@setOnClickListener
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        val userMap = HashMap<String, Any?>()
                        userMap["studentNumber"] = studentNumber
                        userMap["firstName"] = firstName
                        userMap["lastName"] = lastName
                        userMap["middleName"] = middleName
                        userMap["contactNumber"] = contactNumber
                        userMap["email"] = email
                        userMap["role"] = "customer" // Default role as "customer"

                        userId?.let {
                            databaseRef.child("users").child(it).setValue(userMap)
                                .addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        showToast("Registration Successful")
                                        startActivity(Intent(this, LoginActivity::class.java))
                                        finish()
                                    } else {
                                        showToast("Failed to save user data: ${task2.exception?.message}")
                                    }
                                }
                        }
                    } else {
                        showToast("Registration Failed: ${task.exception?.message}")
                    }
                }
        }

        binding.tvAlreadyHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
