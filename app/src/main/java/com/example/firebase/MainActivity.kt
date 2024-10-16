package com.example.firebase

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityMainBinding

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val myReference: DatabaseReference = database.reference.child("MyUsers")

    val userLIst = ArrayList<Users>()
    lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.linearLayout10)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        mainBinding.floatingActionButton.setOnClickListener {

            val intent = Intent(this, AddUserActivity::class.java)
            startActivity(intent)

        }

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id = userAdapter.getUserId(viewHolder.adapterPosition)

                myReference.child(id).removeValue()

                Toast.makeText(applicationContext,"The user was deleted", Toast.LENGTH_SHORT).show()
            }

        }).attachToRecyclerView(mainBinding.recyclerView)


        retrieveDataFromDatabase()

    }

    fun retrieveDataFromDatabase(){
        myReference.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userLIst.clear()

                for (eachUser in snapshot.children){

                    val user = eachUser.getValue(Users::class.java)

                    if(user != null) {
                        println("userId: ${user.userId}")
                        println("userName: ${user.userName}")
                        println("userAge: ${user.userAge}")
                        println("userEmail: ${user.userEmail}")
                        println("********************")

                        userLIst.add(user)
                    }

                    userAdapter = UserAdapter(this@MainActivity, userLIst)

                    mainBinding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

                    mainBinding.recyclerView.adapter = userAdapter

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_delete_all, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.deleteAll){

            showDialogMessage()

        }else if(item.itemId == R.id.signOut){
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    fun showDialogMessage(){

        val dialogMessage = AlertDialog.Builder(this)
        dialogMessage.setTitle("Delete All Users")
        dialogMessage.setMessage("If click yes, All users will be deleted, If you want to delete a specific user, you can swipe on the item.")
        dialogMessage.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->

            dialogInterface.cancel()
        })
        dialogMessage.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
            myReference.removeValue().addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Toast.makeText(applicationContext,"All users were deleted", Toast.LENGTH_SHORT).show()
                }
            }
        })

        dialogMessage.create().show()

    }

}