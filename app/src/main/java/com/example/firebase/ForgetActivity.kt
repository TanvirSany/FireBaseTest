package com.example.firebase

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityForgetBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetActivity : AppCompatActivity() {

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    lateinit var forgetBinding : ActivityForgetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        forgetBinding = ActivityForgetBinding.inflate(layoutInflater)
        val view = forgetBinding.root
        setContentView(view)

        forgetBinding.buttonReset.setOnClickListener {

            val email = forgetBinding.editTextReset.text.toString()

            auth.sendPasswordResetEmail(email).addOnCompleteListener {  task ->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext, "We sent a Password reset mail to your mail address", Toast.LENGTH_SHORT).show()

                    finish()
                }
            }

        }
    }
}