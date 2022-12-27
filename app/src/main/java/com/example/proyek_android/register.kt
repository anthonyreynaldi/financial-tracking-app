package com.example.proyek_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class register : AppCompatActivity() {
    lateinit var et_name: EditText
    lateinit var et_email: EditText
    lateinit var et_phone: EditText
    lateinit var et_pass: EditText
    lateinit var et_confpass: EditText
    var dataUser: ArrayList<user> = ArrayList<user>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        et_name = findViewById<EditText>(R.id.et_name)
        et_email = findViewById<EditText>(R.id.et_email)
        et_phone = findViewById<EditText>(R.id.et_phone)
        et_pass = findViewById<EditText>(R.id.et_password)
        et_confpass = findViewById<EditText>(R.id.et_confpass)
        var btn_register = findViewById<Button>(R.id.btn_register)
        var tv_login = findViewById<TextView>(R.id.tv_login)

        btn_register.setOnClickListener{
            if(et_pass.text.toString() == et_confpass.text.toString()){
                TambahData(db, et_name.text.toString(), et_email.text.toString(), et_phone.text.toString(), et_pass.text.toString())
            }
        }
        tv_login.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    fun TambahData(db: FirebaseFirestore, Name: String, Email: String, Phone: String, Pass: String){
        val userBaru = user(Name, Email, Phone, Pass)
        readData(db)
//        for(int )
        db.collection("user").document(Email)
            .set(userBaru)
            .addOnSuccessListener {
                et_name.setText("")
                et_email.setText("")
                et_phone.setText("")
                et_pass.setText("")
                et_confpass.setText("")
                Log.d("Firebase", "Simpan data berhasil")
            }.addOnFailureListener{
                Log.d("Firebase", it.message.toString())
            }
    }

    fun readData(db: FirebaseFirestore){
        db.collection("user").get()
            .addOnSuccessListener { result ->
                dataUser.clear()
                for(document in result){
                    val namaBaru = user(document.data.get("Name").toString(),
                        document.data.get("Email").toString(),
                        document.data.get("Phone").toString(),
                        document.data.get("Pass").toString())
                    dataUser.add(namaBaru)
                }
            }.addOnFailureListener{
                Log.d("Firebase", it.message.toString())
            }
    }

//    fun getData(db: FirebaseFirestore, Email: String){
//        db.collection("user").get()
//            .addOnSuccessListener { result ->
//                dataUser.clear()
//                for(document in result){
//                    val namaBaru = user(document.data.get("Name").toString(),
//                        document.data.get("Email").toString(),
//                        document.data.get("Phone").toString(),
//                        document.data.get("Pass").toString())
//                    dataUser.add(namaBaru)
//                }
//            }.addOnFailureListener{
//                Log.d("Firebase", it.message.toString())
//            }
//    }
}