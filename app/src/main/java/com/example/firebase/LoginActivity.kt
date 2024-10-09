package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding

    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)


        loginBinding.buttonSignin.setOnClickListener {
            val userEmail = loginBinding.editTextEmailSignup.text.toString()
            val userPassword = loginBinding.editTextPasswordSignup.text.toString()

            signinWithFirebase(userEmail, userPassword)
        }

        loginBinding.buttonSignup.setOnClickListener {

            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)

        }

        loginBinding.buttonForgot.setOnClickListener {
            val intent = Intent(this, ForgetActivity::class.java)
            startActivity(intent)
        }

        loginBinding.buttonSigninWithPhone.setOnClickListener {

            val intent = Intent(this, PhoneActivity::class.java)
            startActivity(intent)
            finish()

        }

    }

    fun signinWithFirebase(userEmail:String, userPassword:String){
        auth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(applicationContext, "Login is successfull", Toast.LENGTH_SHORT).show()

                    val intent = Intent(applicationContext,MainActivity::class.java)
                    startActivity(intent)

                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(applicationContext, task.exception?.toString(), Toast.LENGTH_SHORT).show()

                }
            }
    }

    override fun onStart() {
        super.onStart()

        val user  = auth.currentUser

        if(user != null){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}