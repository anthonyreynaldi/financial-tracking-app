package com.example.proyek_android

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class homepage : AppCompatActivity() {
    companion object{
        var collectionName = "user"
        var documentName = "kell@gmail.com"
        var user = user()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        val db = FirebaseFirestore.getInstance()

        db.collection(homepage.collectionName).document(homepage.documentName).get().addOnCompleteListener {
            if (it.isSuccessful){
                user = it.result.toObject(user::class.java)!!
            }
        }

        var btn_pemasukkan = findViewById<ImageView>(R.id.btn_inppemasukkan)
        var btn_pengeluaran = findViewById<ImageView>(R.id.btn_inppengeluaran)
        var btn_menabung = findViewById<ImageView>(R.id.btn_settargetmenabung)
        var btn_report = findViewById<ImageView>(R.id.btn_report)
        var btn_history = findViewById<ImageView>(R.id.btn_history)
        var btn_kategori = findViewById<ImageView>(R.id.btn_tambahkategori)
        var btn_sumber = findViewById<ImageView>(R.id.btn_tambahsumber)
        var tv_user = findViewById<TextView>(R.id.tv_user)

        btn_sumber.setOnClickListener {
            startActivity(Intent(this, sumberDanaList::class.java))
        }
    }
}