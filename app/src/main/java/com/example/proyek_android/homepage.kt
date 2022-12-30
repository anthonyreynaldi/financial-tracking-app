package com.example.proyek_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class homepage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        var btn_pemasukkan = findViewById<ImageView>(R.id.btn_inppemasukkan)
        var btn_pengeluaran = findViewById<ImageView>(R.id.btn_inppengeluaran)
        var btn_menabung = findViewById<ImageView>(R.id.btn_settargetmenabung)
        var btn_report = findViewById<ImageView>(R.id.btn_report)
        var btn_history = findViewById<ImageView>(R.id.btn_history)
        var btn_kategori = findViewById<ImageView>(R.id.btn_tambahkategori)
        var btn_sumber = findViewById<ImageView>(R.id.btn_tambahkategori)
        var btn_logout = findViewById<ImageView>(R.id.btn_logout)
        var btn_profile = findViewById<ImageView>(R.id.btn_profile)
        var tv_user = findViewById<TextView>(R.id.tv_user)
        var view = findViewById<View>(R.id.homepage_view)

        var sp = getSharedPreferences("user_login", MODE_PRIVATE)
        val isisp = sp.getString("username", null)
        if(isisp == null){
            startActivity(Intent(this, MainActivity::class.java))
        }else{
            tv_user.setText(isisp)
        }

        btn_logout.setVisibility(View.GONE)

        var dropdown = false
        btn_profile.setOnClickListener{
            if (!dropdown){
                btn_logout.setVisibility(View.VISIBLE)
                dropdown = true
            }else{
                btn_logout.setVisibility(View.GONE)
                dropdown = false
            }
        }

        btn_logout.setOnClickListener{
            sp = getSharedPreferences("user_login", MODE_PRIVATE)
            val editor = sp.edit()
            editor.putString("username", null)
            editor.apply()
            startActivity(Intent(this, MainActivity::class.java))
        }

        btn_report.setOnClickListener{
            startActivity(Intent(this, graph_coba::class.java))
        }
    }
    override fun onBackPressed() {
        Log.d("hehe", "hehe")
    }
}