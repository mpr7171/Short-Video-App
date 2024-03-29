package com.example.shortvideoapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.shortvideoapp.ForgotPasswordActivity
import com.example.shortvideoapp.MainActivity
import com.example.shortvideoapp.R
import com.example.shortvideoapp.databinding.ActivityLoginBinding
import com.example.shortvideoapp.signup.SignupActivity1
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val loginButton:Button=findViewById(R.id.loginButton)
        val signupButton:Button=findViewById(R.id.signupButton)
        val forgotPasswordLink: TextView = findViewById(R.id.forgotPasswordLink)

        signupButton.setOnClickListener{
            Intent(this, SignupActivity1::class.java).also{
                startActivity(it)
            }

        }
        resetFields()
        loginButton.setOnClickListener{
            if(checkFields()) {
                login()
            }
        }
        forgotPasswordLink.setOnClickListener{
            Intent(this, ForgotPasswordActivity::class.java).also { // Navigate to ForgotPasswordActivity
                startActivity(it)
            }
        }

    }

    private fun login() {
        auth = Firebase.auth
        val email: EditText =findViewById(R.id.usernameInput)
        val password: EditText =findViewById(R.id.passwordInput)
        auth.signInWithEmailAndPassword(email.text.toString(),password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        this, "Log in successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    val user = auth.currentUser
                    finish()
                    Intent(this,MainActivity::class.java).also {
                        startActivity(it)
                    }
                } else {
                    if (task.exception.toString().contains("com.google.firebase.auth.FirebaseAuthInvalidCredentialsException:")) {
                        Toast.makeText(this,
                            "Incorrect password",
                            Toast.LENGTH_LONG
                        ).show();
                        binding.passwordLayout.requestFocus()
                        binding.passwordInput.setText("")
                    }
                    else if (task.exception.toString().contains("com.google.firebase.auth.FirebaseAuthInvalidUserException:")) {
                        Toast.makeText(this,
                            "There is no account associated with this email address.",
                            Toast.LENGTH_LONG
                        ).show();
                        binding.usernameInput.setText("")
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this, "signInWithEmail:failure ${task.exception}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }

    private fun checkFields():Boolean
    {
        var check:Boolean=true;
        val p: Pattern =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        val m: Matcher = p.matcher(binding.usernameInput.text.toString().trim())
        val emailCheck: Boolean = m.matches()
        if (binding.usernameInput.text.toString().trim().isEmpty()) {
            binding.usernameLayout.error = "This field is required"
            check = false
        } else if (!emailCheck) {
            binding.usernameLayout.error = "Only provide Mahindra email"
            check = false
        }
        if (binding.passwordInput.text.toString().isEmpty()) {
            binding.passwordLayout.error = "This field is required"
            check = false
        }
        // after all validation return true.
        return check
    }

    private fun resetFields() {
        binding.usernameInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable) {
                if (binding.usernameInput.text.toString().trim().isEmpty()) {
                    binding.usernameLayout.error = "This field is required"
                }
                else {
                    binding.usernameLayout.error = null
                }
            }
        })
        binding.passwordInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO Auto-generated method stub
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable) {
                if (binding.passwordInput.text.toString().trim().isEmpty()) {
                    binding.passwordLayout.error = "This field is required"
                }
                else {
                    binding.passwordLayout.error = null
                }
            }
        })
    }

    public override fun onStart() {

        super.onStart()
        auth = Firebase.auth

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            Intent(this, MainActivity::class.java).also{
                finish()
                startActivity(it)
            }
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("moveTaskToBack(true)"))
    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
