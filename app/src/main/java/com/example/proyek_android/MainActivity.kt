package com.example.proyek_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        val et_email = findViewById<EditText>(R.id.et_lgemail)
        val et_password = findViewById<EditText>(R.id.et_lgpassword)
        val tv_register = findViewById<TextView>(R.id.tv_register)
        val btn_login = findViewById<Button>(R.id.btn_login)
        var dataUser: ArrayList<user> = ArrayList<user>()
        val contextView = findViewById<View>(R.id.view2)

        //READ DATA
        db.collection("user").get()
            .addOnSuccessListener { result ->
                dataUser.clear()
                for(document in result){
                    val namaBaru = user(document.data.get("name").toString(),
                        document.data.get("email").toString(),
                        document.data.get("phone").toString(),
                        document.data.get("pass").toString())
                    dataUser.add(namaBaru)
                }
            }.addOnFailureListener{
                Log.d("Firebase", it.message.toString())
            }

        tv_register.setOnClickListener{
            startActivity(Intent(this, register::class.java))
        }


        var sp = getSharedPreferences("user_login", MODE_PRIVATE)
        val isisp = sp.getString("username", null)
        if(isisp != null){
            startActivity(Intent(this, homepage::class.java))
        }

        btn_login.setOnClickListener{
            var bool = false
            for(i in dataUser){
                Log.d("hehe", "msk loop")
                Log.d("hehe", i.Email)
                if(et_email.text.toString() == i.Email){
                    Log.d("hehe", "email nemu")
                    if(et_password.text.toString() == i.Pass){
                        val editor = sp.edit()
                        editor.putString("username", i.Name)
                        editor.apply()
                        startActivity(Intent(this, homepage::class.java))
                        bool = true
                    }
                    break
                }
            }
            if(!bool){
                Toast.makeText(contextView.context,"Email/Password salah", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onBackPressed() {
        Log.d("hehe", "hehe")
    }
}