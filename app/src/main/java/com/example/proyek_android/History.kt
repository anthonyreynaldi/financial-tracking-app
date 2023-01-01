package com.example.proyek_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyek_android.Adapter.AdapterHistory
import com.example.proyek_android.Classes.ItemHistory
import com.google.firebase.firestore.FirebaseFirestore

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
    }

    private fun TambahData() {
        // Add data pengeluaran
        for (item in homepage.user.listPengeluaran) {
            val data = ItemHistory(
                item.nama,
                item.nominal,
                item.kategori,
                item.sumberDana,
                item.tanggal,
                "Pengeluaran"
            )
            arItemHistory.add(data)
        }

        // Add data pemasukkan
        for (item in homepage.user.listPemasukkan) {
            val data = ItemHistory(
                item.nama,
                item.nominal,
                item.kategori,
                item.sumberDana,
                item.tanggal,
                "Pemasukkan"
            )
            arItemHistory.add(data)
        }
    }

    private fun TampilkanData() {
        // LinearLayout
        rvHistory.layoutManager = LinearLayoutManager(this)

        val adapterHistory = AdapterHistory(arItemHistory)
        rvHistory.adapter = adapterHistory
    }
}