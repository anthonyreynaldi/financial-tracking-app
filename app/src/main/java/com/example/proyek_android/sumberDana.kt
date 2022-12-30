package com.example.proyek_android

import android.content.DialogInterface
import android.os.Bundle
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyek_android.Classes.MoneyTextWatcher
import com.example.proyek_android.Classes.SumberDana
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore


class sumberDana : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sumber_dana)

        var user = homepage.user

        val isEdit = intent.getStringExtra("status")
        val index = intent.getIntExtra("index", -1)

        val btn_input = findViewById<Button>(R.id.btn_input_sumber_dana)
        val btn_delete_sumber_dana = findViewById<FloatingActionButton>(R.id.btn_delete_sumber_dana)
        val nama_sumber_dana = findViewById<EditText>(R.id.nama_sumber_dana)
        val tv_msg = findViewById<TextView>(R.id.tv_msg)
        val et_nominal = findViewById<EditText>(R.id.et_nominal_awal)
        et_nominal.addTextChangedListener(MoneyTextWatcher(et_nominal))
        et_nominal.setText("0")

        var sumberDana: SumberDana? = null

        if (isEdit == "edit"){
            sumberDana = user.SumberDana[index]
            nama_sumber_dana.setText(sumberDana.nama)
            et_nominal.setText(sumberDana.jumlah.toString())
            btn_input.setText("save")
            btn_delete_sumber_dana.visibility = View.VISIBLE
        }

        btn_input.setOnClickListener {
            val nama = nama_sumber_dana.text.toString()
            val nominal = et_nominal.text.toString()
            tv_msg.setText("")

            if (nama.isBlank()){
                nama_sumber_dana.setError("Nama harus diisi")
            }

            if (nominal.isBlank()){
                et_nominal.setError("Nominal harus diisi")
            }

            if (nama.isNotBlank() && nominal.isNotBlank()){
                val nominalInt = nominal.replace(".", "").toInt()

                var status = false
                var msg = ""
                var result: Pair<Boolean, String>? = null

                if (isEdit != "edit"){
                    result = user.addSumberDana(nama.uppercase(), nominalInt)
                }else{
                    result = user.editSumberDana(index, nama.uppercase(), nominalInt)
                }

                status = result.first
                msg = result.second

                tv_msg.setText(msg)

                if (status){
                    finish()
                }
            }else{
                tv_msg.setText("Isi nama dan nominal")
            }

        }

        btn_delete_sumber_dana.setOnClickListener {
            if (sumberDana != null) {
                AlertDialog.Builder(this)
                    .setTitle("HAPUS DATA")
                    .setMessage("APAKAH BENAR SUMBER DANA " + sumberDana.nama + " AKAN DIHAPUS?")
                    .setPositiveButton(
                        "HAPUS",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            user.deleteSumberDana(index)
                            finish()
                        }
                    )
                    .setNegativeButton(
                        "BATAL",
                        DialogInterface.OnClickListener { dialogInterface, i ->
                            Toast.makeText(this, "DATA BATAL DIHAPUS", Toast.LENGTH_LONG).show()
                        }
                    ).show()
            }
        }
    }
}