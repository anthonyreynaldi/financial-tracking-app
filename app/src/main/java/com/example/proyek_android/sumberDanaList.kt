package com.example.proyek_android

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.Adapter.AdapterSumberDana
import com.example.proyek_android.Classes.RupiahFormater
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.w3c.dom.Text

class sumberDanaList : AppCompatActivity() {
    private lateinit var adapterSumberDana: AdapterSumberDana
    private lateinit var user: user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sumber_dana_list)

        user = homepage.user

        val rv_sumber_dana = findViewById<RecyclerView>(R.id.rv_sumber_dana)
        val btn_add_sumber_dana = findViewById<FloatingActionButton>(R.id.btn_add_sumber_dana)

        //set RV
        rv_sumber_dana.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adapterSumberDana = AdapterSumberDana(user.SumberDana)
        rv_sumber_dana.adapter = adapterSumberDana
        rv_sumber_dana.addItemDecoration(DividerItemDecoration(baseContext, LinearLayoutManager.VERTICAL))

        setTotal()

        btn_add_sumber_dana.setOnClickListener {
            startActivity(Intent(this, sumberDana::class.java))
        }

        //btn back
        val btn_back = findViewById<ImageView>(R.id.btn_back4)
        btn_back.setOnClickListener{
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapterSumberDana.notifyDataSetChanged()
        setTotal()
    }

    fun setTotal(){
        //set total
        val tv_total_sumber_dana = findViewById<TextView>(R.id.tv_total_sumber_dana)
        val rupiahFormater = RupiahFormater()
        tv_total_sumber_dana.setText("Total: " + rupiahFormater.format(user.ambilTotalSumberDana()))
    }
}