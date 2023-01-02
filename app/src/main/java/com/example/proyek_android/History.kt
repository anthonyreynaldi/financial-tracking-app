package com.example.proyek_android

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.Adapter.AdapterHistory
import com.example.proyek_android.Classes.ItemHistory
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.util.*

class History : AppCompatActivity() {
    private var arItemHistory = arrayListOf<ItemHistory>()
    private lateinit var rvHistory : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        supportActionBar?.hide() // remove toolbar

        rvHistory  = findViewById(R.id.rvHistory)

        TambahData()
        TampilkanData()

        val btn_back = findViewById<ImageView>(R.id.btn_back7)
        btn_back.setOnClickListener{
            startActivity(Intent(this, homepage::class.java))
        }
    }

    private fun TambahData() {
        // Add data pengeluaran
//        for (item in homepage.user.listPengeluaran) {
//            val data = ItemHistory(
//                item.nama,
//                item.nominal,
//                item.kategori,
//                item.sumberDana,
//                item.tanggal,
//                "Pengeluaran"
//            )
//            arItemHistory.add(data)
//        }

        for (i in 0 until homepage.user.listPengeluaran.size) {
            val data = ItemHistory(
                i,
                homepage.user.listPengeluaran[i].nama,
                homepage.user.listPengeluaran[i].nominal,
                homepage.user.listPengeluaran[i].kategori,
                homepage.user.listPengeluaran[i].sumberDana,
                homepage.user.listPengeluaran[i].tanggal,
                homepage.user.listPengeluaran[i].deskripsi,
                "Pengeluaran"
            )
            arItemHistory.add(data)
        }

        // Add data pemasukkan
//        for (item in homepage.user.listPemasukkan) {
//            val data = ItemHistory(
//                item.nama,
//                item.nominal,
//                item.kategori,
//                item.sumberDana,
//                item.tanggal,
//                "Pemasukkan"
//            )
//            arItemHistory.add(data)
//        }

        for (i in 0 until homepage.user.listPemasukkan.size) {
            val data = ItemHistory(
                i,
                homepage.user.listPemasukkan[i].nama,
                homepage.user.listPemasukkan[i].nominal,
                homepage.user.listPemasukkan[i].kategori,
                homepage.user.listPemasukkan[i].sumberDana,
                homepage.user.listPemasukkan[i].tanggal,
                homepage.user.listPemasukkan[i].deskripsi,
                "Pemasukkan"
            )
            arItemHistory.add(data)
        }

        SortByDate()
    }

    private fun TampilkanData() {
        // LinearLayout
        rvHistory.layoutManager = LinearLayoutManager(this)

        val adapterHistory = AdapterHistory(arItemHistory)
        rvHistory.adapter = adapterHistory

        // intent ke detail history
        adapterHistory.setOnItemClickCallback(object : AdapterHistory.OnItemClickCallback{
            override fun onItemClicked(data: ItemHistory) {
                // Toast.makeText(this@History, data.nama, Toast.LENGTH_LONG).show()

                DetailHistory.item = data  // set companion object
                startActivity(Intent(this@History, DetailHistory::class.java))
            }
        })
    }

    // function untuk sorting history secara ascending
    private fun SortByDate() {
        for (i in 0 until arItemHistory.size-1) {
            for (j in 0 until arItemHistory.size-i-1) {
                val d1: String? = arItemHistory[j].tanggal
                val d2: String? = arItemHistory[j+1].tanggal

                val simpleDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy")
                val firstDate: Date? = simpleDateFormat.parse(d1)
                val secondDate: Date? = simpleDateFormat.parse(d2)

                if (firstDate != null) {
                    if (firstDate.after(secondDate)){
                        val temp = arItemHistory[j]
                        arItemHistory[j] = arItemHistory[j+1]
                        arItemHistory[j+1] = temp
                    }
                }
            }
        }
    }
}