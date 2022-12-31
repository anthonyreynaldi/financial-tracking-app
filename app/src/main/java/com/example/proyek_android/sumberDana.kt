package com.example.proyek_android

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.proyek_android.Classes.MoneyTextWatcher
import com.example.proyek_android.Classes.SumberDana
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.File
import java.time.LocalDateTime


class sumberDana : AppCompatActivity() {
    var storageRef: StorageReference = Firebase.storage.getReference()
    var iconPath : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sumber_dana)

        var user = homepage.user

        val isEdit = intent.getStringExtra("status")
        val index = intent.getIntExtra("index", -1)

        val icon = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.icon_sumber_dana)
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

            sumberDana.icon?.let {
                iconPath = sumberDana.icon
                storageRef.child(it).downloadUrl.addOnSuccessListener {
                    Glide.with(this).load(it).into(icon)
                }
            }
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
                    result = user.addSumberDana(nama.uppercase(), nominalInt, iconPath)
                }else{
                    result = user.editSumberDana(index, nama.uppercase(), nominalInt, iconPath)
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

        icon.setOnClickListener {
            val intent = Intent(this, pilihGambar::class.java)
            startActivityForResult(intent, 234)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 234 && resultCode == Activity.RESULT_OK && data != null) {
            // Get the Uri of data
            val file_uri = data.getStringExtra("path")
            Log.d("RESULT IMGAE", "onActivityResult: ${file_uri}")

            val icon = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.icon_sumber_dana)

            if (file_uri != null) {
                iconPath = file_uri
                storageRef.child(file_uri).downloadUrl.addOnSuccessListener {
                    Glide.with(this).load(it).into(icon)
                }
            }
        }
    }
}