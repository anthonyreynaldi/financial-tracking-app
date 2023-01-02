package com.example.proyek_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.proyek_android.Classes.MoneyTextWatcher
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class target : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target)

        var user = homepage.user

        val et_target_pengeluaran = findViewById<EditText>(R.id.et_target_pengeluaran)
        val et_target_tabungan = findViewById<EditText>(R.id.et_target_tabungan)
        val btn_simpan = findViewById<Button>(R.id.btn_simpan_target)

        et_target_pengeluaran.addTextChangedListener(MoneyTextWatcher(et_target_pengeluaran))
        et_target_tabungan.addTextChangedListener(MoneyTextWatcher(et_target_tabungan))

        et_target_pengeluaran.setText(user.targetPengeluaran.toString())
        et_target_tabungan.setText(user.targetTabungan.toString())

        btn_simpan.setOnClickListener {
            val target_pengeluaran = et_target_pengeluaran.text.toString()
            val target_tabungan = et_target_tabungan.text.toString()

            if (target_pengeluaran.isBlank()){
                et_target_pengeluaran.setError("Target pengeluaran harus diisi")
            }

            if (target_tabungan.isBlank()){
                et_target_tabungan.setError("Target tabungan harus diisi")
            }

            if (target_tabungan.isNotBlank() && target_pengeluaran.isNotBlank()){
                val target_pengeluaran_int = target_pengeluaran.replace(".", "").toInt()
                val target_tabungan_int = target_tabungan.replace(".", "").toInt()

                user.set_TargetPengeluaran(target_pengeluaran_int)
                user.set_TargetTabungan(target_tabungan_int)

                Toast.makeText(this, "Target berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
        }

        val min_pengeluaran = findViewById<ImageView>(R.id.tv_m_pengeluaran)
        val plus_pengeluaran = findViewById<ImageView>(R.id.tv_p_pengeluaran)

//        val decimalFormat = DecimalFormat("#,###,###")
//        val prezzo: String = decimalFormat.format(number)
        var pengeluaran = user.targetPengeluaran.toString().toInt()
        min_pengeluaran.setOnClickListener{
            if(pengeluaran > 0){
                et_target_pengeluaran.setText((pengeluaran - 100000).toString())
                pengeluaran = pengeluaran - 100000
            }else if(pengeluaran > 2000000){
                et_target_pengeluaran.setText((pengeluaran - 500000).toString())
                pengeluaran = pengeluaran - 500000
            }else if(pengeluaran > 10000000){
                et_target_pengeluaran.setText((pengeluaran - 1000000).toString())
                pengeluaran = pengeluaran - 1000000
            }
        }
        plus_pengeluaran.setOnClickListener{
            if(pengeluaran >= 0){
                et_target_pengeluaran.setText((pengeluaran + 100000).toString())
                pengeluaran = pengeluaran + 100000
            }else if(pengeluaran > 2000000){
                et_target_pengeluaran.setText((pengeluaran + 500000).toString())
                pengeluaran = pengeluaran + 500000
            }else if(pengeluaran > 10000000){
                et_target_pengeluaran.setText((pengeluaran + 1000000).toString())
                pengeluaran = pengeluaran + 1000000
            }
        }


        val min_tabungan = findViewById<ImageView>(R.id.tv_m_tabungan)
        val plus_tabungan = findViewById<ImageView>(R.id.tv_p_tabungan)
        var tabungan = user.targetTabungan.toString().toInt()
        min_tabungan.setOnClickListener{
            if(tabungan > 0){
                et_target_tabungan.setText((tabungan - 100000).toString())
                tabungan = tabungan - 100000
            }else if(tabungan > 2000000){
                et_target_tabungan.setText((tabungan - 500000).toString())
                tabungan = tabungan - 500000
            }else if(tabungan > 10000000){
                et_target_tabungan.setText((tabungan - 1000000).toString())
                tabungan = tabungan - 1000000
            }
        }
        plus_tabungan.setOnClickListener{
            if(tabungan >= 0){
                et_target_tabungan.setText((tabungan + 100000).toString())
                tabungan = tabungan + 100000
            }else if(tabungan > 2000000){
                et_target_tabungan.setText((tabungan + 500000).toString())
                tabungan = tabungan + 500000
            }else if(tabungan > 10000000){
                et_target_tabungan.setText((tabungan + 1000000).toString())
                tabungan = tabungan + 1000000
            }
        }

        val btn_back = findViewById<ImageView>(R.id.btn_back5)
        btn_back.setOnClickListener{
            finish()
        }
    }
}