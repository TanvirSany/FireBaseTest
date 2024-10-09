package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.ActivityPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {

    lateinit var phoneBinding : ActivityPhoneBinding

    lateinit var mCallback : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    var verificationCode = ""

    val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        phoneBinding = ActivityPhoneBinding.inflate(layoutInflater)
        val view = phoneBinding.root
        setContentView(view)


        phoneBinding.buttonSendSMSCode.setOnClickListener {

            val userPhoneNumber = phoneBinding.editTextPhoneNumber.text.toString()

            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(userPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this@PhoneActivity)
                .setCallbacks(mCallback)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)

        }

        phoneBinding.buttonVerify.setOnClickListener {

            signinWithSMSCode()
        }

        mCallback = object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(p0: FirebaseException) {

            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)

                verificationCode = p0

            }

        }
    }

    fun signinWithSMSCode(){

        val userEnterCode = phoneBinding.editTextVerificationCode.text.toString()

        val credential = PhoneAuthProvider.getCredential(verificationCode,userEnterCode)

        signinWithPhoneAuthCredential(credential)

    }

    fun signinWithPhoneAuthCredential(credential: PhoneAuthCredential){

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if(task.isSuccessful){

                val intent = Intent(this@PhoneActivity,MainActivity::class.java)
                startActivity(intent)
                finish()

            }else{
                Toast.makeText(applicationContext,"The code you entered is incorrect",Toast.LENGTH_SHORT).show()
            }
        }

    }
}