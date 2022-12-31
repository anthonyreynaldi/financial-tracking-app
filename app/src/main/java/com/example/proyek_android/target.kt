package com.example.proyek_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.proyek_android.Classes.MoneyTextWatcher

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

                user.setTargetPengeluaran(target_pengeluaran_int)
                user.setTargetTabungan(target_tabungan_int)

                Toast.makeText(this, "Target berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}