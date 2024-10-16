package com.example.firebase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityUpdateUserBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID

class UpdateUserActivity : AppCompatActivity() {

    lateinit var updateUserBinding : ActivityUpdateUserBinding

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference : DatabaseReference = database.reference.child("MyUsers")

    var imageUri : Uri? = null

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = firebaseStorage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updateUserBinding = ActivityUpdateUserBinding.inflate(layoutInflater)
        val view = updateUserBinding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayout10)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.title= "Update User"

        registerActivityForResult()

        getAndSetData()

        updateUserBinding.buttonUpdateUser.setOnClickListener {

            uploadPhoto()

        }

        updateUserBinding.userUpdateProfileImage.setOnClickListener {

            chooseImage()

        }

    }

    fun uploadPhoto(){
        updateUserBinding.buttonUpdateUser.isClickable = false
        updateUserBinding.progressBarUpdateUser.visibility = View.VISIBLE

        val imageName = intent.getStringExtra("imageName").toString()

        val imageReference = storageReference.child("images").child(imageName)

        imageUri?.let { uri ->

            imageReference.putFile(uri).addOnCompleteListener{

                Toast.makeText(applicationContext, "Image updated", Toast.LENGTH_SHORT).show()

                val myUploadImageReference = storageReference.child("images").child(imageName)
                myUploadImageReference.downloadUrl.addOnSuccessListener { url ->

                    val imageURL = url.toString()
                    updateData(imageURL , imageName)


                }

            }.addOnFailureListener{

                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()

            }

        }
    }


    fun registerActivityForResult(){
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
            , ActivityResultCallback { result ->

                val resultCode = result.resultCode
                val imageData = result.data

                if(resultCode == RESULT_OK && imageData != null){

                    imageUri = imageData.data

                    imageUri?.let {
                        Picasso.get().load(it).into(updateUserBinding.userUpdateProfileImage)
                    }


                }

            })
    }

    fun getAndSetData(){
        val name = intent.getStringExtra("name")
        val age = intent.getIntExtra( "age", 0).toString()
        val email = intent.getStringExtra("email")
        val imageUlr = intent.getStringExtra("imageUrl").toString()


        updateUserBinding.editTextUpdateName.setText(name)
        updateUserBinding.editTextUpdateAge.setText(age)
        updateUserBinding.editTextUpdateEmail.setText(email)

        Picasso.get().load(imageUlr).into(updateUserBinding.userUpdateProfileImage)

    }

    fun updateData(imageUrl: String, imageName:String){
        val updateName = updateUserBinding.editTextUpdateName.text.toString()
        val updateAge = updateUserBinding.editTextUpdateAge.text.toString().toInt()
        val updateEmail = updateUserBinding.editTextUpdateEmail.text.toString()
        val userId = intent.getStringExtra("id").toString()

        val userMap = mutableMapOf<String,Any>()
        userMap["userId"] = userId
        userMap["userName"]= updateName
        userMap["userAge"]= updateAge
        userMap["userEmail"]= updateEmail
        userMap["url"] = imageUrl
        userMap["imageName"]= imageName

        myReference.child(userId).updateChildren(userMap).addOnCompleteListener { task->
            if(task.isSuccessful){
                Toast.makeText(applicationContext,"The user has been updated", Toast.LENGTH_SHORT).show()

                updateUserBinding.buttonUpdateUser.isClickable = true
                updateUserBinding.progressBarUpdateUser.visibility = View.INVISIBLE

                finish()
            }
        }

    }

    fun chooseImage(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1)
            }
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

        }else{

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)


        }
    }
}