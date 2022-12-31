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

        et_name = findViewById<EditText>(R.id.nama_sumber_dana)
        et_email = findViewById<EditText>(R.id.et_email)
        et_phone = findViewById<EditText>(R.id.et_phone)
        et_pass = findViewById<EditText>(R.id.et_password)
        et_confpass = findViewById<EditText>(R.id.et_confpass)
        var btn_register = findViewById<Button>(R.id.btn_register)
        var tv_login = findViewById<TextView>(R.id.tv_login)
        val contextView = findViewById<View>(R.id.view)

        var sp = getSharedPreferences("user_login", MODE_PRIVATE)
        val isisp = sp.getString("username", null)
        if(isisp != null){
            startActivity(Intent(this, homepage::class.java))
            finishAffinity()
        }

        btn_register.setOnClickListener{
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

            if(et_name.text.toString() != "" && et_email.text.toString() != "" &&
                et_phone.text.toString() != "" && et_pass.text.toString() != ""){
                if(et_pass.text.toString() == et_confpass.text.toString()){
                    var bool = false
                    for(i in dataUser){
                        if(et_email.text.toString() == i.Email){
                            bool = true
                            break
                        }
                    }
                    if(!bool){
                        TambahData(db, et_name.text.toString(), et_email.text.toString().lowercase(), et_phone.text.toString(), et_pass.text.toString())
                        sp = getSharedPreferences("user_login", MODE_PRIVATE)
                        val editor = sp.edit()
                        editor.putString("username", et_name.text.toString())
                        editor.putString("email", et_email.text.toString())
                        editor.apply()
                        startActivity(Intent(this, homepage::class.java))
                        finishAffinity()
                    }else{
                        //email is taken
                        Toast.makeText(contextView.context,"Email sudah terdaftar",Toast.LENGTH_LONG).show()
                    }
                }else{
                    //pass and conf pass doesn't match
                    Toast.makeText(contextView.context,"Password dan Confirm Password tidak sama",Toast.LENGTH_LONG).show()
                }
            }else{
                //ada yang belum diisi
                Toast.makeText(contextView.context,"Mohon isi semua field",Toast.LENGTH_LONG).show()

            }

        }
        tv_login.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    fun readData(db: FirebaseFirestore){
        db.collection("user").get()
            .addOnSuccessListener { result ->
                dataUser.clear()
                for(document in result){
                    val namaBaru = user(document.data.get("name").toString(),
                        document.data.get("email").toString(),
                        document.data.get("phone").toString(),
                        document.data.get("pass").toString())
                    dataUser.add(namaBaru)
                    Log.d("hehe1", dataUser.size.toString())
                }
            }.addOnFailureListener{
                Log.d("Firebase", it.message.toString())
            }
    }

    fun TambahData(db: FirebaseFirestore, Name: String, Email: String, Phone: String, Pass: String){
        val userBaru = user(Name, Email, Phone, Pass)
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