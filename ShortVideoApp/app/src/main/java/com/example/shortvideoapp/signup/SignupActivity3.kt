package com.example.shortvideoapp.signup
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.shortvideoapp.MainActivity
import com.example.shortvideoapp.R
import com.example.shortvideoapp.databinding.ActivitySignup3Binding
import com.example.shortvideoapp.firebasefunctions.databaseURL
import com.example.shortvideoapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class SignupActivity3 : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivitySignup3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignup3Binding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val signupButton:Button=findViewById(R.id.signupButton)

        resetFields()
        signupButton.setOnClickListener {
            if(checkFields()) {
                signup()
            }
        }
    }
    private fun signup()
    {
        binding.signupButton.text="Signing Up"
        binding.progressBarSignUp.show()
        binding.signupButton.isEnabled=false
        val firstName = intent.getStringExtra("firstname").toString()
        val lastName = intent.getStringExtra("lastname").toString()
        val email = intent.getStringExtra("emailName").toString()
        val userName = intent.getStringExtra("userName").toString()
        val password:EditText=findViewById(R.id.passwordInput)


        auth = Firebase.auth
        database = FirebaseDatabase.getInstance(databaseURL).reference

        auth.createUserWithEmailAndPassword(email,password.text.toString()).addOnCompleteListener(this) {

            if (it.isSuccessful) {

                writeNewUser(auth.currentUser!!.uid, userName, firstName , lastName,email)


            } else {
                if (it.exception.toString().contains("com.google.firebase.auth.FirebaseAuthUserCollisionException:")) {
                    Toast.makeText(this,
                        "The email address is already in use by another account.",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                }
                else {
                    Toast.makeText(
                        this, it.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    fun writeNewUser(uid:String,userName:String,firstName:String,lastName:String,email:String) {
        val user = User(userName, firstName,lastName)

        fun generateProfilePictureAndUpload() {
            val baseColor = Color.WHITE
            val red = (baseColor.red + (0..256).random()) / 2
            val green = (baseColor.green + (0..256).random()) / 2
            val blue = (baseColor.blue + (0..256).random()) / 2
            val color = Color.rgb(red, green, blue)

            val bmp: Bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
            val canvas = Canvas(bmp);
            canvas.drawColor(color)

            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val storageRef = Firebase.storage

            val ref = storageRef.reference.child("images/$email/profilePicture")
            val uploadTask = ref.putBytes(data)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    user.profilePicture = downloadUri.toString()
                    database.child("users").child(uid).setValue(user).addOnSuccessListener {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
        }
        generateProfilePictureAndUpload()
    }

    private fun checkFields():Boolean
    {
        var check:Boolean=true;
        if (binding.passwordInput.text.toString().isEmpty()) {
            binding.passwordLayout.error = "This field is required"
            check = false
        }
        else if (binding.passwordInput.length() < 6) {
            binding.passwordLayout.error = "Password must be at least 6 characters"
            check = false
        }
        if (binding.passwordInput.text.toString() != binding.confirmPasswordInput.text.toString()) {
            binding.confirmPasswordLayout.error = "Passwords do not match"
            check = false
        }
        // after all validation return true.
        return check
    }

    private fun resetFields() {
        binding.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable) {
                if (binding.passwordInput.text.toString().isEmpty()) {
                    binding.passwordLayout.error = "This field is required"
                }
                else if (binding.passwordInput.length() < 6) {
                    binding.passwordLayout.error = "Password must be at least 6 characters"
                }
                else {
                    binding.passwordLayout.error = null
                }
            }
        })
        binding.confirmPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable) {
                binding.confirmPasswordLayout.error = null
            }
        })
    }
}

