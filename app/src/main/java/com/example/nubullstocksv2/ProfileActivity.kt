package com.example.nubullstocksv2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.nubullstocksv2.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var s3Client: AmazonS3Client
    private lateinit var transferUtility: TransferUtility

    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
    private val bucketName = "nubullstocks"
    private val awsRegion = "ap-southeast-2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid ?: return

        database = FirebaseDatabase.getInstance().getReference("users").child(userId)
        setupAWS()
        loadUserProfile()

        binding.profileImage.setOnClickListener { openImagePicker() }
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        //  Bottom Navigation Setup
        binding.bottomNavigation.selectedItemId = R.id.menu_profile
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.menu_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.menu_profile -> true // Stay on the same screen
                else -> false
            }
        }
    }

    private fun setupAWS() {
        val credentialsProvider = CognitoCachingCredentialsProvider(
            applicationContext,
            "ap-southeast-2:d1dd8673-bf97-4bbc-b2c8-807a4d0ea6d0", // Replace with AWS Cognito ID
            Regions.AP_SOUTHEAST_2
        )
        s3Client = AmazonS3Client(credentialsProvider)
        transferUtility = TransferUtility(s3Client, applicationContext)
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data?.data != null) {
            imageUri = data.data
            uploadImageToAWS()
        }
    }

    private fun uploadImageToAWS() {
        val userId = auth.currentUser?.uid ?: return
        imageUri?.let { uri ->
            val key = "profile_pictures/$userId.jpg"
            val file = File(cacheDir, "profile.jpg")
            val inputStream = contentResolver.openInputStream(uri)
            file.outputStream().use { output -> inputStream?.copyTo(output) }

            val uploadObserver = transferUtility.upload(bucketName, key, file)

            uploadObserver.setTransferListener(object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    if (state == TransferState.COMPLETED) {
                        val imageUrl = "https://$bucketName.s3.$awsRegion.amazonaws.com/$key"
                        database.child("profileImageUrl").setValue(imageUrl)
                            .addOnSuccessListener {
                                // Load circle profile image after upload
                                Glide.with(this@ProfileActivity)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_profile)
                                    .error(R.drawable.ic_profile)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .circleCrop() // Circle Profile Image
                                    .into(binding.profileImage)

                                Toast.makeText(this@ProfileActivity, "Profile updated!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@ProfileActivity, "Failed to update database: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}
                override fun onError(id: Int, ex: Exception?) {
                    Toast.makeText(this@ProfileActivity, "Upload failed: ${ex?.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firstName = snapshot.child("firstName").getValue(String::class.java) ?: ""
                val lastName = snapshot.child("lastName").getValue(String::class.java) ?: ""
                val middleName = snapshot.child("middleName").getValue(String::class.java) ?: ""
                val fullName = "$firstName $middleName $lastName".trim()
                val studentNumber = snapshot.child("studentNumber").getValue(String::class.java) ?: "N/A"
                val contactNumber = snapshot.child("contactNumber").getValue(String::class.java) ?: "N/A"
                val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)

                binding.tvStudentName.text = fullName
                binding.tvStudentId.text = studentNumber
                binding.tvContactNumber.text = contactNumber

                if (!profileImageUrl.isNullOrEmpty()) {
                    // Load circle profile image after upload
                    Glide.with(this@ProfileActivity)
                        .load(profileImageUrl)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .circleCrop() // 🔴 Ginagawang bilog ang image
                        .into(binding.profileImage)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
